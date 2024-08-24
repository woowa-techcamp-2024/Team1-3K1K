package camp.woowak.lab.web.dao.store;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.infra.cache.FakeMenuStockCacheService;
import camp.woowak.lab.infra.cache.MenuStockCacheService;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.order.domain.PriceChecker;
import camp.woowak.lab.order.domain.SingleStoreOrderValidator;
import camp.woowak.lab.order.domain.StockRequester;
import camp.woowak.lab.order.domain.WithdrawPointService;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.order.repository.OrderRepository;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreAddress;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

public abstract class StoreDummiesFixture {
	protected final StoreRepository storeRepository;
	protected final StoreCategoryRepository storeCategoryRepository;
	protected final MenuRepository menuRepository;
	protected final VendorRepository vendorRepository;
	protected final PayAccountRepository payAccountRepository;
	protected final OrderRepository orderRepository;
	protected final CustomerRepository customerRepository;
	protected final PasswordEncoder passwordEncoder;
	protected final MenuStockCacheService menuStockCacheService;

	public StoreDummiesFixture(StoreRepository storeRepository, StoreCategoryRepository storeCategoryRepository,
							   VendorRepository vendorRepository, PayAccountRepository payAccountRepository,
							   OrderRepository orderRepository,
							   CustomerRepository customerRepository, MenuRepository menuRepository) {
		this.storeRepository = storeRepository;
		this.storeCategoryRepository = storeCategoryRepository;
		this.vendorRepository = vendorRepository;
		this.payAccountRepository = payAccountRepository;
		this.orderRepository = orderRepository;
		this.customerRepository = customerRepository;
		this.passwordEncoder = new NoOpPasswordEncoder();
		this.menuRepository = menuRepository;
		this.menuStockCacheService = new FakeMenuStockCacheService();
	}

	protected List<Customer> createDummyCustomers(int numberOfCustomers) {
		List<Customer> customers = new ArrayList<>(numberOfCustomers);
		for (int i = 0; i < numberOfCustomers; i++) {
			PayAccount payAccount = new PayAccount();
			payAccountRepository.save(payAccount);
			Customer customer = new Customer("customer " + i, "cemail" + i + "@gmail.com", "password1234!",
				"010-1234-5678", payAccount, passwordEncoder);
			customers.add(customer);
		}

		return customerRepository.saveAllAndFlush(customers);
	}

	protected List<Order> createOrdersWithRandomCount(List<Store> store) {
		List<Customer> dummyCustomers = createDummyCustomers(10);
		List<Order> orders = new ArrayList<>(store.size());
		int[] orderCount = new int[store.size()];
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < orderCount.length; i++) {
			orderCount[i] = random.nextInt(50);
		}
		for (int i = 0; i < store.size(); i++) {
			Store s = store.get(i);
			SingleStoreOrderValidator singleStoreOrderValidator = new TestSingleStoreOrderValidator(s, storeRepository);
			for (int j = 0; j < orderCount[i]; j++) {
				Customer customer = dummyCustomers.get(
					new Random(System.currentTimeMillis()).nextInt(dummyCustomers.size()));
				Order order = new Order(customer, new ArrayList<>(), singleStoreOrderValidator,
					new StockRequester(menuRepository, menuStockCacheService), new TestPriceChecker(menuRepository),
					new TestWithdrawPointService(payAccountRepository), LocalDateTime.now());
				orders.add(order);
			}
		}
		return orderRepository.saveAllAndFlush(orders);
	}

	protected List<Store> createDummyStores(int numberOfStores) {
		List<Store> stores = new ArrayList<>(numberOfStores);
		Random random = new Random(System.currentTimeMillis());
		Vendor vendor = createDummyVendor();
		for (int i = 0; i < numberOfStores; i++) {
			StoreCategory storeCategory = createRandomDummyStoreCategory();
			String name = "Store " + (i + 1);
			String address = StoreAddress.DEFAULT_DISTRICT;
			String phoneNumber = "123-456-789" + (i % 10);
			Integer minOrderPrice = 5000 + (random.nextInt(10000)) / 1000 * 1000;
			LocalDateTime startTime = LocalDateTime.now().plusHours(random.nextInt(10)).withSecond(0).withNano(0);
			LocalDateTime endTime = startTime.plusHours(random.nextInt(20) + 1);

			Store store = new Store(vendor, storeCategory, name, address, phoneNumber, minOrderPrice, startTime,
				endTime);
			stores.add(store);
		}

		storeRepository.saveAllAndFlush(stores);
		return stores;
	}

	protected Vendor createDummyVendor() {
		PayAccount payAccount = new PayAccount();
		payAccountRepository.saveAndFlush(payAccount);
		return vendorRepository.saveAndFlush(
			new Vendor("VendorName", "email@gmail.com", "Password123!", "010-1234-5678",
				payAccount, passwordEncoder));
	}

	protected StoreCategory createRandomDummyStoreCategory() {
		return storeCategoryRepository.saveAndFlush(new StoreCategory(UUID.randomUUID().toString()));
	}

	private class TestPriceChecker extends PriceChecker {
		public TestPriceChecker(MenuRepository menuRepository) {
			super(menuRepository);
		}

		@Override
		public List<OrderItem> check(Store store, List<CartItem> cartItems) {
			return List.of();
		}
	}

	private class TestWithdrawPointService extends WithdrawPointService {

		public TestWithdrawPointService(PayAccountRepository payAccountRepository) {
			super(payAccountRepository);
		}

		@Override
		public List<OrderItem> withdraw(Customer customer, List<OrderItem> orderItems) {
			return List.of();
		}
	}

	private class TestSingleStoreOrderValidator extends SingleStoreOrderValidator {
		private final Store store;

		public TestSingleStoreOrderValidator(Store store, StoreRepository storeRepository) {
			super(storeRepository);
			this.store = store;
		}

		@Override
		public Store check(List<CartItem> cartItems) {
			return store;
		}
	}
}
