package camp.woowak.lab.order.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLong;
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

	//
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
			int numberOfThreads = customers.size();    // 고객 수만큼 멀티스레딩
			ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
			CountDownLatch latch = new CountDownLatch(numberOfThreads);

			Menu targetMenu = menus.get(0);  // 첫 번째 메뉴를 대상으로 테스트
			Long initialStock = targetMenu.getStockCount();
			AtomicInteger successCount = new AtomicInteger();

			// when
			for (int i = 0; i < numberOfThreads; i++) {
				final int index = i;
				executorService.submit(() -> {
					try {
						Customer customer = customers.get(index);
						cartService.addMenu(new AddCartCommand(customer.getId().toString(), targetMenu.getId()));
						OrderCreationCommand command = new OrderCreationCommand(customer.getId());
						orderCreationService.create(command);
						successCount.incrementAndGet();
					} catch (Exception e) {
						log.info("exception", e);
					} finally {
						latch.countDown();
					}
				});
			}

			latch.await(10, TimeUnit.SECONDS);
			RAtomicLong cachedStock = redissonClient.getAtomicLong(
				RedisCacheConstants.MENU_STOCK_PREFIX + targetMenu.getId());
			em.clear();

			// then
			assertThat(successCount.get()).isEqualTo(numberOfThreads);
			assertThat(cachedStock.get()).isEqualTo(initialStock - numberOfThreads);
			Menu findMenu = getStockCountFromDB(targetMenu.getId());
			assertThat(findMenu.getStockCount()).isEqualTo(initialStock - numberOfThreads);
		}

		/**
		 * 시나리오:
		 * 고객1: [메뉴1:1개, 메뉴2:1개, 메뉴3:1개]
		 * 고객2: [메뉴1:1개, 메뉴2:1개, 메뉴3:1개]
		 * 고객3: [메뉴1:1개, 메뉴2:1개, 메뉴3:1개]
		 * 음식 상품: [메뉴1:3개, 메뉴2:3개, 메뉴3:2개]
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
			Thread.sleep(100L);

			int numberOfThreads = 2;
			ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
			CountDownLatch latch = new CountDownLatch(numberOfThreads);

			Menu menu1 = menus.get(0);
			Menu menu2 = menus.get(1);

			AtomicInteger successfulOrders = new AtomicInteger(0);

			// when
			for (int i = 0; i < numberOfThreads; i++) {
				final int index = i;
				executorService.submit(() -> {
					try {
						orderCreationService.create(new OrderCreationCommand(customers.get(index).getId()));
						// successfulOrders.incrementAndGet();
					} catch (RuntimeException e) {
						// 주문 실패 (재고 부족 등의 이유)
						log.info("exception", e);
					} finally {
						latch.countDown();
					}
				});
			}

			latch.await(10, TimeUnit.MINUTES);
			executorService.shutdown();
			// then
			// then: Redis Cache 정합성
			assertThat(redissonClient.getAtomicLong(
				RedisCacheConstants.MENU_STOCK_PREFIX + menu1.getId()).get()).isEqualTo(1);
			assertThat(redissonClient.getAtomicLong(
				RedisCacheConstants.MENU_STOCK_PREFIX + menu2.getId()).get()).isEqualTo(0);

			Menu updatedMenu1 = getStockCountFromDB(menu1.getId());
			Menu updatedMenu2 = getStockCountFromDB(menu2.getId());

			log.info("menu 1 stock {}", updatedMenu1.getStockCount());
			log.info("menu 2 stock {}", updatedMenu2.getStockCount());

			assertThat(updatedMenu1.getStockCount()).isEqualTo(1);
			assertThat(updatedMenu2.getStockCount()).isEqualTo(0);
		}
	}

	/**
	 * 테스트 시나리오: 계좌 잔액 부족으로 인한 롤백 시나리오
	 */
	@Test
	@DisplayName("")
	void test1() {
		// given

		// when

		// then
	}

	/**
	 * 테스트 시나리오: 가게 최소 주문 금액 미만으로 인한 주문 롤백 시나리오
	 * 고객1: [메뉴1:1개, 메뉴2:1개, 메뉴3:1개]
	 * 고객2: [메뉴1:1개, 메뉴2:1개, 메뉴3:1개]
	 * 고객3: [메뉴1:1개, 메뉴2:1개, 메뉴3:1개]
	 * 음식 상품: [메뉴1:3개, 메뉴2:3개, 메뉴3:2개]
	 */
	@Test
	@DisplayName("")
	void test2() {
		// given

		// when

		// then
	}

	PayAccount savePayAccount() {
		PayAccount payAccount = new PayAccount();
		payAccount.charge(1000000L);
		return payAccountRepository.saveAndFlush(payAccount);
	}

	Vendor saveVendor(PayAccount payAccount) {
		vendor = new Vendor(
			"점주1",
			"점주1@gmail.com",
			"123456789"
			, "010-1111-1111",
			payAccount,
			new NoOpPasswordEncoder());

		return vendorRepository.saveAndFlush(vendor);
	}

	List<Customer> saveCustomers(List<PayAccount> payAccounts) {
		List<Customer> customers = new ArrayList<>();
		for (int id = 0; id < payAccounts.size(); id++) {
			PayAccount payAccount = payAccounts.get(id);
			Customer customer = new Customer(
				id + "고객",
				id + "@gmail.com",
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
