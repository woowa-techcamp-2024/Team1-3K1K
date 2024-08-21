package camp.woowak.lab.web.dao.order;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.querydsl.jpa.impl.JPAQueryFactory;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.infra.date.DateTimeProvider;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.order.domain.PriceChecker;
import camp.woowak.lab.order.domain.SingleStoreOrderValidator;
import camp.woowak.lab.order.domain.StockRequester;
import camp.woowak.lab.order.domain.WithdrawPointService;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.config.QuerydslConfig;
import camp.woowak.lab.web.dto.response.order.OrderResponse;
import jakarta.persistence.EntityManager;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@Import({QueryDslOrderDao.class, QuerydslConfig.class})
class QueryDslOrderDaoTest {
	@Autowired
	private QueryDslOrderDao queryDslOrderDao;

	@Autowired
	private JPAQueryFactory queryFactory;

	@Autowired
	private EntityManager entityManager;

	private Vendor vendor1;
	private Vendor vendor2;

	@BeforeEach
	void setUp() {

		// 테스트에 사용할 엔티티 생성 및 저장
		vendor1 = createVendor(entityManager, "vendor1@example.com");
		vendor2 = createVendor(entityManager, "vendor2@example.com");
		Customer customer1 = createCustomer(entityManager, "customer1@example.com");
		Customer customer2 = createCustomer(entityManager, "customer2@example.com");

		StoreCategory storeCategory = createStoreCategory(entityManager, "Asian");
		DateTimeProvider fixedStartTime = () -> LocalDateTime.of(2024, 8, 24, 1, 0, 0);
		DateTimeProvider fixedEndTime = () -> LocalDateTime.of(2024, 8, 24, 5, 0, 0);
		Store store1 = createStore(entityManager, vendor1, storeCategory, "Store 1", fixedStartTime.now(),
			fixedEndTime.now());
		Store store2 = createStore(entityManager, vendor1, storeCategory, "Store 2", fixedStartTime.now().plusHours(2),
			fixedEndTime.now().plusHours(2));
		Store store3 = createStore(entityManager, vendor2, storeCategory, "Store 3", fixedStartTime.now().plusHours(4),
			fixedEndTime.now().plusHours(2));

		MenuCategory menuCategory1_1 = createMenuCategory(entityManager, store1, "main menu");
		MenuCategory menuCategory1_2 = createMenuCategory(entityManager, store1, "sub menu");
		MenuCategory menuCategory2_1 = createMenuCategory(entityManager, store2, "main menu");
		MenuCategory menuCategory2_2 = createMenuCategory(entityManager, store2, "sub menu");
		MenuCategory menuCategory3_1 = createMenuCategory(entityManager, store3, "main menu");
		MenuCategory menuCategory3_2 = createMenuCategory(entityManager, store3, "sub menu");

		Menu menu1 = createMenu(store1, menuCategory1_1, "Menu 1");
		Menu menu2 = createMenu(store1, menuCategory1_2, "Menu 2");
		Menu menu3 = createMenu(store2, menuCategory2_1, "Menu 3");
		Menu menu4 = createMenu(store2, menuCategory2_2, "Menu 4");
		Menu menu5 = createMenu(store3, menuCategory3_1, "Menu 3");
		Menu menu6 = createMenu(store3, menuCategory3_2, "Menu 3");

		List<OrderItem> orderItems1 = List.of(new OrderItem(menu1.getId(), 1000, 2),
			new OrderItem(menu2.getId(), 20000, 3));
		List<OrderItem> orderItems2 = List.of(new OrderItem(menu3.getId(), 1000, 2),
			new OrderItem(menu4.getId(), 20000, 3));
		List<OrderItem> orderItems3 = List.of(new OrderItem(menu5.getId(), 1000, 2),
			new OrderItem(menu6.getId(), 20000, 3));

		Order order1 = createOrder(entityManager, customer1, store1, orderItems1);
		Order order2 = createOrder(entityManager, customer1, store2, orderItems2);
		Order order3 = createOrder(entityManager, customer1, store3, orderItems3);
		Order order4 = createOrder(entityManager, customer2, store1, orderItems1);
		Order order5 = createOrder(entityManager, customer2, store2, orderItems2);
		Order order6 = createOrder(entityManager, customer2, store3, orderItems3);
	}

	@Nested
	@DisplayName("")
	class FindAllIs {
		@Test
		@DisplayName("")
		void FilteredWithVendorId() {
			UUID vendor1Id = vendor1.getId();
			DateTimeProvider fixedStartTime = () -> LocalDateTime.of(2024, 8, 24, 1, 0, 0);
			OrderQuery orderQuery = new OrderQuery(null, null, null, vendor1Id);
			PageRequest pageRequest = PageRequest.of(0, 10);
			Page<OrderResponse> response = queryDslOrderDao.findAll(orderQuery, pageRequest);
			Assertions.assertThat(response).isNotNull();
			Assertions.assertThat(response.getContent()).hasSize(2);
		}
	}

	private Order createOrder(EntityManager entityManager, Customer customer, Store store, List<OrderItem> orderItems) {
		SingleStoreOrderValidator singleStoreOrderValidator = mock(SingleStoreOrderValidator.class);
		StockRequester stockRequester = mock(StockRequester.class);
		PriceChecker priceChecker = mock(PriceChecker.class);
		WithdrawPointService withdrawPointService = mock(WithdrawPointService.class);

		when(singleStoreOrderValidator.check(any())).thenReturn(store);
		when(priceChecker.check(any(Store.class), anyList())).thenReturn(orderItems);

		DateTimeProvider fixedTime = () -> LocalDateTime.of(2024, 8, 24, 1, 0, 0);
		Order order = new Order(customer, Collections.EMPTY_LIST, singleStoreOrderValidator, stockRequester,
			priceChecker, withdrawPointService, fixedTime.now());
		entityManager.persist(order);
		return order;
	}

	private Menu createMenu(Store store, MenuCategory menuCategory, String name) {
		Menu menu = new Menu(store, menuCategory, name, 10000L, 10L, "imageurl:" + name);
		entityManager.persist(menu);
		return menu;
	}

	private MenuCategory createMenuCategory(EntityManager entityManager, Store store, String name) {
		MenuCategory menuCategory = new MenuCategory(store, name);
		entityManager.persist(menuCategory);
		return menuCategory;
	}

	private Store createStore(EntityManager entityManager, Vendor vendor, StoreCategory storeCategory,
							  String storeName, LocalDateTime startTime, LocalDateTime endTime) {

		Store store = new Store(vendor, storeCategory, storeName, "송파", "123-1234", 10000,
			startTime, endTime);
		entityManager.persist(store);
		return store;
	}

	private StoreCategory createStoreCategory(EntityManager entityManager, String name) {
		StoreCategory storeCategory = new StoreCategory(name);
		entityManager.persist(storeCategory);
		return storeCategory;
	}

	private Customer createCustomer(EntityManager entityManager, String email) {
		PayAccount customerPayAccount = new PayAccount();
		entityManager.persist(customerPayAccount);
		Customer customer = new Customer("Customer Name", email, "password123", "123-4567", customerPayAccount,
			new NoOpPasswordEncoder());
		entityManager.persist(customer);
		return customer;
	}

	private Vendor createVendor(EntityManager entityManager, String email) {
		PayAccount vendorPayAccount = new PayAccount();
		entityManager.persist(vendorPayAccount);
		Vendor vendor = new Vendor("Vendor Name", email, "VendorPassword", "010-0000-0000", vendorPayAccount,
			new NoOpPasswordEncoder());
		entityManager.persist(vendor);
		return vendor;
	}
}