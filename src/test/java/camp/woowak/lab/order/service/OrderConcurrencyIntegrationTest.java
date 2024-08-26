package camp.woowak.lab.order.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.repository.InMemoryCartRepository;
import camp.woowak.lab.cart.service.CartService;
import camp.woowak.lab.cart.service.command.AddCartCommand;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.infra.cache.redis.RedisCacheConstants;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.menu.repository.MenuCategoryRepository;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.order.repository.OrderRepository;
import camp.woowak.lab.order.service.command.OrderCreationCommand;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import jakarta.persistence.EntityManager;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderConcurrencyIntegrationTest {

	private static final Logger log = LoggerFactory.getLogger(OrderConcurrencyIntegrationTest.class);
	/**
	 * 통합 테스트 대상
	 */
	@Autowired
	CartService cartService;

	@Autowired
	OrderCreationService orderCreationService;

	/**
	 * Test Data Setup 용
	 */
	@Autowired
	PayAccountRepository payAccountRepository;

	@Autowired
	VendorRepository vendorRepository;

	@Autowired
	StoreRepository storeRepository;

	@Autowired
	StoreCategoryRepository storeCategoryRepository;

	@Autowired
	private MenuCategoryRepository menuCategoryRepository;

	@Autowired
	private MenuRepository menuRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private InMemoryCartRepository inMemoryCartRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	EntityManager em;

	StoreCategory storeCategory;
	Store store;

	MenuCategory menuCategory;
	List<Menu> menus;

	PayAccount vendorPayAccount;
	Vendor vendor;

	List<PayAccount> payAccountsOfCustomers;
	List<Customer> customers;
	List<Cart> cartOfCustomers;

	@BeforeEach
	void setup() {
		redissonClient.getKeys().flushall();
		// 점주 Fixture 셋업
		vendorPayAccount = savePayAccount();
		vendor = saveVendor(vendorPayAccount);

		// 가게 Fixture 셋업
		storeCategory = saveStoreCategory("가게카테고리");
		store = saveStore(vendor, storeCategory);

		// 음식상품 메뉴 Fixture 셋업
		menuCategory = saveMenusCategory(store, "메뉴카테고리1");

		// 고객 Fixture 셋업
		payAccountsOfCustomers = List.of(
			savePayAccount(),
			savePayAccount(),
			savePayAccount()
		);
		customers = saveCustomers(payAccountsOfCustomers);
		cartOfCustomers = saveCart(customers);
	}

	@AfterEach
	void clean() {
		redissonClient.getKeys().flushall();
	}

	@Nested
	@DisplayName("카트 담기, 주문 동시성 통합 테스트")
	class TestClass {

		@Test
		@DisplayName("N명이 동시에 주문했을 때 음식 상품 메뉴의 재고수가 N개 감소한다.")
		void concurrentOrderTest() throws InterruptedException {
			menus = List.of(saveMenu(store, menuCategory, "메뉴1", 5000L, 10L));

			// given
			int totalCustomerCount = customers.size();    // 고객 수만큼 멀티스레딩
			ExecutorService executorService = Executors.newFixedThreadPool(totalCustomerCount);
			CountDownLatch latch = new CountDownLatch(totalCustomerCount);

			Menu targetMenu = menus.get(0);  // 첫 번째 메뉴를 대상으로 테스트
			Long initialStock = targetMenu.getStockCount();

			// when
			for (int customerCount = 0; customerCount < totalCustomerCount; customerCount++) {
				Customer customer = customers.get(customerCount);
				executorService.submit(() -> executeAddCartAndOrderCreation(latch, customer, targetMenu));
			}
			latch.await(10, TimeUnit.SECONDS);
			executorService.shutdown();

			// then: Redis Cache 정합성
			assertThat(redissonClient.getAtomicLong(
				RedisCacheConstants.MENU_STOCK_PREFIX + targetMenu.getId()).get())
				.isEqualTo(initialStock - totalCustomerCount);

			// then: verify RDB
			Menu findMenu = getStockCountFromDB(targetMenu.getId());
			assertThat(findMenu.getStockCount()).isEqualTo(initialStock - totalCustomerCount);
		}

		/**
		 * 시나리오:
		 * 고객1: [메뉴1:1개, 메뉴2:1개]
		 * 고객2: [메뉴1:1개, 메뉴2:1개]
		 * 음식 상품: [메뉴1:2개, 메뉴2:1개]
		 */
		@Test
		@DisplayName("여러 고객이 동시에 주문할 때, 재고 부족 시 롤백되어야 한다.")
		void concurrentOrderWithLimitedStockTest() throws InterruptedException {
			// given
			menus = List.of(
				saveMenu(store, menuCategory, "메뉴1", 5000L, 2L),
				saveMenu(store, menuCategory, "메뉴2", 5000L, 1L)    // 여기서 터진다.
			);

			for (int index = 0; index < customers.size(); index++) {
				Customer customer = customers.get(index);
				for (Menu menu : menus) {// 각 메뉴를 1개씩 담음
					cartService.addMenu(new AddCartCommand(customer.getId().toString(), menu.getId()));
				}
			}

			int numberOfThreads = 2;
			ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
			CountDownLatch latch = new CountDownLatch(numberOfThreads);
			Menu menu1 = menus.get(0);
			Menu menu2 = menus.get(1);

			// when
			for (int personCount = 0; personCount < numberOfThreads; personCount++) {
				final int index = personCount;
				executorService.submit(() -> executeOrderCreation(latch, customers.get(index)));
			}
			latch.await(10, TimeUnit.MINUTES);
			executorService.shutdown();

			// then: Redis Cache 정합성
			assertThat(redissonClient.getAtomicLong(
				RedisCacheConstants.MENU_STOCK_PREFIX + menu1.getId()).get()).isEqualTo(1);
			assertThat(redissonClient.getAtomicLong(
				RedisCacheConstants.MENU_STOCK_PREFIX + menu2.getId()).get()).isEqualTo(0);

			// then: verify RDB
			Menu updatedMenu1 = getStockCountFromDB(menu1.getId());
			Menu updatedMenu2 = getStockCountFromDB(menu2.getId());

			assertThat(updatedMenu1.getStockCount()).isEqualTo(1);
			assertThat(updatedMenu2.getStockCount()).isEqualTo(0);
		}
	}

	/**
	 * Test Scenario: 계좌 잔액 부족으로 인한 롤백 시나리오
	 * 고객1: [메뉴1:3개, 메뉴2:3개, 메뉴3:3개]	잔액: 100000원
	 * 고객2: [메뉴1:1개, 메뉴2:1개, 메뉴3:1개]	잔액: 100000원
	 * 고객3: [메뉴1:1개, 메뉴2:1개, 메뉴3:1개]	잔액: 5000원	--> 잔액 부족으로 실패
	 * 음식 상품: [메뉴1:5개, 메뉴2:5개, 메뉴3:5개]
	 *
	 * expected:
	 * Redis 음식 재고수: [메뉴1:1개, 메뉴2:1개, 메뉴3:1개]
	 * RDB 음식 재고수: [메뉴1:1개, 메뉴2:1개, 메뉴3:1개]
	 */
	@Test
	@DisplayName("계좌 잔액 부족으로 주문 실패시, 캐싱된 재고수와 RDB 재고수가 롤백된다.")
	void insufficientBalanceExceptionThenRollback() throws Throwable {
		// given
		int menu1ExpectedCount = 1;
		int menu2ExpectedCount = 1;
		int menu3ExpectedCount = 1;

		// given
		menus = List.of(
			saveMenu(store, menuCategory, "메뉴1", 10000L, 5L),
			saveMenu(store, menuCategory, "메뉴2", 10000L, 5L),
			saveMenu(store, menuCategory, "메뉴3", 10000L, 5L)
		);
		Menu menu1 = menus.get(0);
		Menu menu2 = menus.get(1);
		Menu menu3 = menus.get(2);

		customers = List.of(
			saveCustomer(
				savePayAccount(100000),
				"고오객1", "고오객1@gmail.com"
			),
			saveCustomer(
				savePayAccount(100000),
				"고오객2", "고오객2@gmail.com"
			),
			saveCustomer(
				savePayAccount(5000),
				"고오객3", "고오객3@gmail.com"
			)
		);
		Customer customer1 = customers.get(0);
		Customer customer2 = customers.get(1);
		Customer customer3 = customers.get(2);

		// 고객1: [메뉴1:3개, 메뉴2:3개, 메뉴3:3개]
		setupMenuToCart(customer1, menu1, 3);
		setupMenuToCart(customer1, menu2, 3);
		setupMenuToCart(customer1, menu3, 3);

		// 고객2: [메뉴1:1개, 메뉴2:1개, 메뉴3:1개]
		setupMenuToCart(customer2, menu1, 1);
		setupMenuToCart(customer2, menu2, 1);
		setupMenuToCart(customer2, menu3, 1);

		// 고객3: [메뉴1:1개, 메뉴2:1개, 메뉴3:1개]
		setupMenuToCart(customer3, menu1, 1);
		setupMenuToCart(customer3, menu2, 1);
		setupMenuToCart(customer3, menu3, 1);

		// when
		int numberOfThreads = 3;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);
		for (int personCount = 0; personCount < numberOfThreads; personCount++) {
			int personIdx = personCount;
			executorService.submit(() -> executeOrderCreation(latch, customers.get(personIdx)));
		}
		latch.await(10, TimeUnit.MINUTES);
		executorService.shutdown();

		// then: verify RDB
		Menu updatedMenu1 = getStockCountFromDB(menu1.getId());
		Menu updatedMenu2 = getStockCountFromDB(menu2.getId());
		Menu updatedMenu3 = getStockCountFromDB(menu3.getId());

		assertThat(updatedMenu1.getStockCount()).isEqualTo(menu1ExpectedCount);
		assertThat(updatedMenu2.getStockCount()).isEqualTo(menu2ExpectedCount);
		assertThat(updatedMenu3.getStockCount()).isEqualTo(menu3ExpectedCount);

		// then: verify Redis
		assertThat(redissonClient.getAtomicLong(
			RedisCacheConstants.MENU_STOCK_PREFIX + menu1.getId()).get()).isEqualTo(menu1ExpectedCount);
		assertThat(redissonClient.getAtomicLong(
			RedisCacheConstants.MENU_STOCK_PREFIX + menu2.getId()).get()).isEqualTo(menu2ExpectedCount);
		assertThat(redissonClient.getAtomicLong(
			RedisCacheConstants.MENU_STOCK_PREFIX + menu2.getId()).get()).isEqualTo(menu3ExpectedCount);
	}

	/**
	 * Test Scenario: 가게 최소 주문 금액 미만으로 인한 주문 롤백 시나리오
	 * 고객1: [메뉴1:3개, 메뉴2:3개, 메뉴3:3개]
	 * 고객2: [메뉴1:1개, 메뉴2:1개]	--> 최수 주문 금액 미만으로 실패
	 * 고객3: [메뉴1:1개]	--> 최수 주문 금액 미만으로 실패
	 * 고객4: [메뉴1:1개, 메뉴2:2개, 메뉴3:3개]
	 *
	 * 가게 최소 주문 금액: 5,000원
	 * 각 메뉴 가격: 1,000원, 각 메뉴 개수: 10개
	 *
	 * expected:
	 * Redis 음식 재고수: [메뉴1:6개, 메뉴2:5개, 메뉴3:4개]
	 * RDB 음식 재고수: [메뉴1:6개, 메뉴2:5개, 메뉴3:4개]
	 */
	@Test
	@DisplayName("최소 주문 금액 미만으로 주문 실패시, 캐싱된 재고수와 RDB 재고수가 롤백된다.")
	void minOrderPriceExceptionThenRollback() throws Throwable {
		// given
		int menu1ExpectedCount = 6;
		int menu2ExpectedCount = 5;
		int menu3ExpectedCount = 4;

		// given: 각 메뉴 가격: 1,000원, 각 메뉴 개수: 10개
		menus = List.of(
			saveMenu(store, menuCategory, "메뉴1", 1000L, 10L),
			saveMenu(store, menuCategory, "메뉴2", 1000L, 10L),
			saveMenu(store, menuCategory, "메뉴3", 1000L, 10L)
		);
		Menu menu1 = menus.get(0);
		Menu menu2 = menus.get(1);
		Menu menu3 = menus.get(2);

		customers = List.of(
			saveCustomer(
				savePayAccount(100000),
				"고오객1", "고오객1@gmail.com"
			),
			saveCustomer(
				savePayAccount(100000),
				"고오객2", "고오객2@gmail.com"
			),
			saveCustomer(
				savePayAccount(100000),
				"고오객3", "고오객3@gmail.com"
			),
			saveCustomer(
				savePayAccount(100000),
				"고오객4", "고오객4@gmail.com"
			)
		);
		Customer customer1 = customers.get(0);
		Customer customer2 = customers.get(1);
		Customer customer3 = customers.get(2);
		Customer customer4 = customers.get(3);

		// 고객1: [메뉴1:3개, 메뉴2:3개, 메뉴3:3개]
		setupMenuToCart(customer1, menu1, 3);
		setupMenuToCart(customer1, menu2, 3);
		setupMenuToCart(customer1, menu3, 3);

		// 고객2: [메뉴1:1개, 메뉴2:1개]	--> 최수 주문 금액 미만으로 실패
		setupMenuToCart(customer2, menu1, 1);
		setupMenuToCart(customer2, menu2, 1);

		// 고객3: [메뉴1:1개]	--> 최수 주문 금액 미만으로 실패
		setupMenuToCart(customer3, menu1, 1);

		// 고객4: [메뉴1:1개, 메뉴2:2개, 메뉴3:3개]
		setupMenuToCart(customer4, menu1, 1);
		setupMenuToCart(customer4, menu2, 2);
		setupMenuToCart(customer4, menu3, 3);

		// when
		int numberOfThreads = 4;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);
		for (int personCount = 0; personCount < numberOfThreads; personCount++) {
			int personIdx = personCount;
			executorService.submit(() -> executeOrderCreation(latch, customers.get(personIdx)));
		}
		latch.await(10, TimeUnit.MINUTES);
		executorService.shutdown();

		// then: verify RDB
		Menu updatedMenu1 = getStockCountFromDB(menu1.getId());
		Menu updatedMenu2 = getStockCountFromDB(menu2.getId());
		Menu updatedMenu3 = getStockCountFromDB(menu3.getId());

		assertThat(updatedMenu1.getStockCount()).isEqualTo(menu1ExpectedCount);
		assertThat(updatedMenu2.getStockCount()).isEqualTo(menu2ExpectedCount);
		assertThat(updatedMenu3.getStockCount()).isEqualTo(menu3ExpectedCount);

		// then: verify Redis
		assertThat(redissonClient.getAtomicLong(
			RedisCacheConstants.MENU_STOCK_PREFIX + menu1.getId()).get()).isEqualTo(menu1ExpectedCount);
		assertThat(redissonClient.getAtomicLong(
			RedisCacheConstants.MENU_STOCK_PREFIX + menu2.getId()).get()).isEqualTo(menu2ExpectedCount);
		assertThat(redissonClient.getAtomicLong(
			RedisCacheConstants.MENU_STOCK_PREFIX + menu3.getId()).get()).isEqualTo(menu3ExpectedCount);
	}

	void setupMenuToCart(Customer customer, Menu menu, int addCount) {
		for (int count = 0; count < addCount; count++) {
			cartService.addMenu(new AddCartCommand(customer.getId().toString(), menu.getId()));
		}
	}

	private void executeOrderCreation(CountDownLatch latch, Customer customer) {
		try {
			orderCreationService.create(new OrderCreationCommand(customer.getId()));
		} catch (Exception e) {
			log.info("exception", e);
		} finally {
			latch.countDown();
		}
	}

	private void executeAddCartAndOrderCreation(CountDownLatch latch, Customer customer, Menu targetMenu) {
		try {
			cartService.addMenu(new AddCartCommand(customer.getId().toString(), targetMenu.getId()));
			orderCreationService.create(new OrderCreationCommand(customer.getId()));
		} catch (Exception e) {
			log.info("exception", e);
		} finally {
			latch.countDown();
		}
	}

	PayAccount savePayAccount(long chargeAmount) {
		PayAccount payAccount = new PayAccount();
		payAccount.charge(chargeAmount);
		return payAccountRepository.saveAndFlush(payAccount);
	}

	PayAccount savePayAccount() {
		PayAccount payAccount = new PayAccount();
		payAccount.charge(1000000L);
		return payAccountRepository.saveAndFlush(payAccount);
	}

	Vendor saveVendor(PayAccount payAccount) {
		vendor = new Vendor(
			"저엄주1",
			"저엄주1@gmail.com",
			"123456789"
			, "010-1111-1111",
			payAccount,
			new NoOpPasswordEncoder());

		return vendorRepository.saveAndFlush(vendor);
	}

	Customer saveCustomer(PayAccount payAccount, String name, String email) {
		Customer customer = new Customer(name, email, "123456789", "010-1111-1111", payAccount,
			new NoOpPasswordEncoder());

		return customerRepository.saveAndFlush(customer);
	}

	List<Customer> saveCustomers(List<PayAccount> payAccounts) {
		List<Customer> customers = new ArrayList<>();
		for (int id = 0; id < payAccounts.size(); id++) {
			PayAccount payAccount = payAccounts.get(id);
			Customer customer = new Customer(
				id + "고객",
				id + "고오오객@gmail.com",
				"123456789",
				"010-1111-1111",
				payAccount,
				new NoOpPasswordEncoder());
			customers.add(customer);
		}

		return customerRepository.saveAll(customers);
	}

	StoreCategory saveStoreCategory(String name) {
		StoreCategory storeCategory = new StoreCategory(name);
		return storeCategoryRepository.saveAndFlush(storeCategory);
	}

	Store saveStore(Vendor vendor, StoreCategory storeCategory) {
		store = new Store(
			vendor,
			storeCategory,
			"가게이름",
			"송파",
			"02-1111-1111",
			5000,
			LocalDate.now().atTime(0, 0),
			LocalDate.now().atTime(23, 59)
		);

		return storeRepository.saveAndFlush(store);
	}

	MenuCategory saveMenusCategory(Store store, String name) {
		MenuCategory menuCategory = new MenuCategory(store, name);

		return menuCategoryRepository.saveAndFlush(menuCategory);
	}

	Menu saveMenu(Store store, MenuCategory menuCategory, String name, Long price, Long stockCount) {
		Menu menu = new Menu(
			store,
			menuCategory,
			name,
			price,
			stockCount,
			"image_" + name);
		return menuRepository.saveAndFlush(menu);
	}

	List<Cart> saveCart(List<Customer> customers) {
		List<Cart> carts = new ArrayList<>();
		for (Customer customer : customers) {
			Cart cart = new Cart(customer.getId().toString());
			carts.add(cart);
			inMemoryCartRepository.save(cart);
		}

		return carts;
	}

	private Menu getStockCountFromDB(Long menuId) {
		String query = "SELECT m FROM Menu m WHERE m.id = :menuId";
		return em.createQuery(query, Menu.class)
			.setParameter("menuId", menuId)
			.getSingleResult();
	}

}
