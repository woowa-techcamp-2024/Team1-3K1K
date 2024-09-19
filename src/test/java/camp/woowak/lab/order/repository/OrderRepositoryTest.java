package camp.woowak.lab.order.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.infra.cache.MenuStockCacheService;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.menu.repository.MenuCategoryRepository;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.order.domain.OrderFactory;
import camp.woowak.lab.order.domain.PriceChecker;
import camp.woowak.lab.order.domain.SingleStoreOrderValidator;
import camp.woowak.lab.order.domain.StockRequester;
import camp.woowak.lab.order.domain.WithdrawPointService;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;

@SpringBootTest
@Transactional
class OrderRepositoryTest {
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private StoreCategoryRepository storeCategoryRepository;

	@Autowired
	private PayAccountRepository payAccountRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private MenuRepository menuRepository;

	@Autowired
	private MenuCategoryRepository menuCategoryRepository;

	@Autowired
	MenuStockCacheService menuStockCacheService;

	private OrderFactory orderFactory;

	private Store store1;

	private Store store2;

	private Vendor vendor;

	private Vendor differentVendor;

	private Customer customer;

	private Menu menu1;

	private Menu menu2;

	@BeforeEach
	void setUp() {
		orderFactory = new OrderFactory(new SingleStoreOrderValidator(storeRepository),
			new StockRequester(menuRepository, menuStockCacheService), new PriceChecker(menuRepository),
			new WithdrawPointService(payAccountRepository), LocalDateTime::now);
		StoreCategory storeCategory = storeCategoryRepository.save(new StoreCategory("storeCategory"));
		vendor = vendorRepository.saveAndFlush(
			new Vendor("vendor", "vendor@email.com", "password", "010-1234-5678",
				payAccountRepository.save(new PayAccount()),
				new NoOpPasswordEncoder()));
		differentVendor = vendorRepository.saveAndFlush(
			new Vendor("differentVendor", "differentVendor@email.com", "password", "010-1234-5678",
				payAccountRepository.save(new PayAccount()), new NoOpPasswordEncoder()));
		PayAccount customerPayAccount = new PayAccount();
		customerPayAccount.charge(1000000L);
		customer = customerRepository.saveAndFlush(
			new Customer("customer", "customer@email.com", "password", "010-1234-5678",
				payAccountRepository.save(customerPayAccount), new NoOpPasswordEncoder()));
		store1 = storeRepository.saveAndFlush(
			new Store(vendor, storeCategory, "store", "송파", "010-1234-5678", 10000, LocalDate.now().atTime(9, 0),
				LocalDate.now().atTime(21, 0)));

		store2 = storeRepository.saveAndFlush(
			new Store(vendor, storeCategory, "store", "송파", "010-1234-5678", 10000, LocalDate.now().atTime(9, 0),
				LocalDate.now().atTime(21, 0)));
		menu1 = menuRepository.saveAndFlush(
			new Menu(store1, menuCategoryRepository.saveAndFlush(new MenuCategory(store1, "메인")), "menu1", 10000L, 100L,
				"image"));
		menu2 = menuRepository.saveAndFlush(
			new Menu(store2, menuCategoryRepository.saveAndFlush(new MenuCategory(store2, "메인")), "menu2", 10000L, 100L,
				"image"));
	}

	@Disabled
	@Test
	@DisplayName("점주 주문 조회 테스트 - 성공")
	void testFindAllByOwner() {
		// given
		List<CartItem> store1CartItems = List.of(new CartItem(menu1.getId(), store1.getId(), 1));
		orderRepository.saveAndFlush(
			orderFactory.createOrder(customer, store1CartItems));
		List<CartItem> store2CartItems = List.of(new CartItem(menu1.getId(), store2.getId(), 1));
		orderRepository.saveAndFlush(
			orderFactory.createOrder(customer, store2CartItems));
		// when
		List<Order> orders = orderRepository.findAllByOwner(vendor.getId());

		// then
		assertEquals(2, orders.size());
	}

	@Disabled
	@Test
	@DisplayName("점주 주문 조회 테스트 - 권한 없는 점주 실패")
	void testFindAllByOwnerFailWithUnauthorized() {
		// given
		// when
		List<Order> orders = orderRepository.findAllByOwner(differentVendor.getId());

		// then
		assertEquals(0, orders.size());
	}

	@Disabled
	@Test
	@DisplayName("점주 특정 매장 주문 조회 테스트 - 성공")
	void testFindByStore() {
		// given
		List<CartItem> store1CartItems = List.of(new CartItem(menu1.getId(), store1.getId(), 1));
		Order order = orderRepository.saveAndFlush(
			orderFactory.createOrder(customer, store1CartItems));
		List<CartItem> store2CartItems = List.of(new CartItem(menu1.getId(), store2.getId(), 1));
		orderRepository.saveAndFlush(
			orderFactory.createOrder(customer, store2CartItems));

		// when
		List<Order> orders = orderRepository.findByStore(store1.getId(), vendor.getId());

		// then
		assertEquals(1, orders.size());
		assertEquals(order.getId(), orders.get(0).getId());
	}

	@Disabled
	@Test
	@DisplayName("점주 특정 매장 주문 조회 테스트 - 권한 없는 점주 실패")
	void testFindByStoreFailWithUnauthorized() {
		// given
		// when
		List<Order> orders = orderRepository.findByStore(store1.getId(), differentVendor.getId());

		// then
		assertEquals(0, orders.size());
	}
}
