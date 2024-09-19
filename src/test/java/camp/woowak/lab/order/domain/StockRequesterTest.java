package camp.woowak.lab.order.domain;

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
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.cart.repository.InMemoryCartRepository;
import camp.woowak.lab.cart.service.CartService;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.menu.exception.NotEnoughStockException;
import camp.woowak.lab.menu.repository.MenuCategoryRepository;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.order.repository.OrderRepository;
import camp.woowak.lab.order.service.OrderCreationService;
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
class StockRequesterTest {

	private static final Logger log = LoggerFactory.getLogger(StockRequesterTest.class);
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
	private StockRequester stockRequester;

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

	@Test
	@DisplayName("싱글 스레드 - 음식 상품 메뉴 재고 부족 시, DB 롤백 테스트")
	@Transactional
	void test() {
		// given
		menus = List.of(saveMenu(store, menuCategory, "메뉴1", 5000L, 10L),
			saveMenu(store, menuCategory, "메뉴2", 5000L, 5L));
		Menu menu1 = menus.get(0);
		Menu menu2 = menus.get(1);

		Cart cart = cartOfCustomers.get(0);
		cart.addMenu(menu1, 10);
		cart.addMenu(menu2, 6);       // 재고 보다 많게 카트에 담음

		List<CartItem> cartItems = cart.getCartItems();
		assertThatThrownBy(() -> stockRequester.request(cartItems)).isInstanceOf(NotEnoughStockException.class);

		Menu findMenu1 = getStockCountFromDB(menu1.getId());
		Menu findMenu2 = getStockCountFromDB(menu2.getId());
		assertThat(findMenu1.getStockCount()).isEqualTo(10L);
		assertThat(findMenu2.getStockCount()).isEqualTo(5L);
	}

	@Test
	@DisplayName("싱글 스레드 - 음식 상품 메뉴 재고 부족 시, DB 롤백 테스트")
	void test2() {
		// given
		menus = List.of(saveMenu(store, menuCategory, "메뉴1", 5000L, 10L),
			saveMenu(store, menuCategory, "메뉴2", 5000L, 5L));
		Menu menu1 = menus.get(0);
		Menu menu2 = menus.get(1);

		Cart cart1 = cartOfCustomers.get(0);
		cart1.addMenu(menu1, 9);
		cart1.addMenu(menu2, 5);

		Cart cart2 = cartOfCustomers.get(1);
		cart2.addMenu(menu1, 1);
		cart2.addMenu(menu2, 1);

		List<CartItem> cartItems1 = cart1.getCartItems();
		List<CartItem> cartItems2 = cart2.getCartItems();

		// when
		stockRequester.request(cartItems1);
		assertThatThrownBy(() -> stockRequester.request(cartItems2)).isInstanceOf(NotEnoughStockException.class);
		// DB 싱크 스케줄링 변경으로 인한 주석 처리
		// Menu findMenu1 = getStockCountFromDB(menu1.getId());
		// Menu findMenu2 = getStockCountFromDB(menu2.getId());
		// assertThat(findMenu1.getStockCount()).isEqualTo(1L);
		// assertThat(findMenu2.getStockCount()).isEqualTo(0L);
	}

	@Test
	@DisplayName("멀티 스레드 - 음식 상품 메뉴 재고 부족 시, DB 롤백 테스트")
	void test3() throws InterruptedException {
		// given
		menus = List.of(saveMenu(store, menuCategory, "메뉴1", 5000L, 10L),
			saveMenu(store, menuCategory, "메뉴2", 5000L, 5L));
		Menu menu1 = menus.get(0);
		Menu menu2 = menus.get(1);

		Cart cart1 = cartOfCustomers.get(0);
		cart1.addMenu(menu1, 5);
		cart1.addMenu(menu2, 5);

		Cart cart2 = cartOfCustomers.get(1);
		cart2.addMenu(menu1, 6);
		cart2.addMenu(menu2, 1);

		Cart cart3 = cartOfCustomers.get(2);
		cart3.addMenu(menu1, 1);    // 재고 부족
		cart3.addMenu(menu2, 1);    // 재고 부족

		Cart cart4 = cartOfCustomers.get(3);
		cart4.addMenu(menu1, 2);
		cart4.addMenu(menu2, 2);

		List<List<CartItem>> cartItems = List.of(
			cart1.getCartItems(),
			cart2.getCartItems(),
			cart3.getCartItems(),
			cart4.getCartItems());

		CountDownLatch latch = new CountDownLatch(4);
		ExecutorService executorService = Executors.newFixedThreadPool(4);

		// when
		for (int personCount = 0; personCount < 4; personCount++) {
			final int personCountIdx = personCount;
			executorService.execute(() -> {
				try {
					List<CartItem> request = stockRequester.request(cartItems.get(personCountIdx));
					log.info("success request cartItem = {}", request);
				} catch (RuntimeException e) {
					// 주문 실패 (재고 부족 등의 이유)
					log.info("exception", e);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await(10, TimeUnit.SECONDS);
		executorService.shutdown();

		// stockRequester.request(cartItems1);
		// assertThatThrownBy(() -> stockRequester.request(cartItems2)).isInstanceOf(NotEnoughStockException.class);

		Menu findMenu1 = getStockCountFromDB(menu1.getId());
		Menu findMenu2 = getStockCountFromDB(menu2.getId());
		log.info("findMenu1 stockCount = {}", findMenu1.getStockCount());
		log.info("findMenu2 stockCount = {}", findMenu2.getStockCount());
	}

	PayAccount savePayAccount() {
		PayAccount payAccount = new PayAccount();
		payAccount.charge(1000000L);
		return payAccountRepository.saveAndFlush(payAccount);
	}

	Vendor saveVendor(PayAccount payAccount) {
		vendor = new Vendor(
			"저어엄주1",
			"저어엄주1@gmail.com",
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
		Menu menu = createMenu(store, menuCategory, name, price, stockCount);
		return menuRepository.saveAndFlush(menu);
	}

	Menu createMenu(Store store, MenuCategory menuCategory, String name, Long price, Long stockCount) {
		return new Menu(
			store,
			menuCategory,
			name,
			price,
			stockCount,
			"image_" + name);
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
