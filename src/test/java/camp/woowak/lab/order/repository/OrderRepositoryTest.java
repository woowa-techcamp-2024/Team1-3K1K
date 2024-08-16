package camp.woowak.lab.order.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.order.domain.Order;
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

	private Store store1;

	private Store store2;

	private Vendor vendor;

	private Vendor differentVendor;

	private Customer customer;

	@BeforeEach
	void setUp() {
		StoreCategory storeCategory = storeCategoryRepository.save(new StoreCategory("storeCategory"));
		vendor = vendorRepository.saveAndFlush(
			new Vendor("vendor", "vendor@email.com", "password", "010-1234-5678",
				payAccountRepository.save(new PayAccount()),
				new NoOpPasswordEncoder()));
		differentVendor = vendorRepository.saveAndFlush(
			new Vendor("differentVendor", "differentVendor@email.com", "password", "010-1234-5678",
				payAccountRepository.save(new PayAccount()), new NoOpPasswordEncoder()));
		customer = customerRepository.saveAndFlush(
			new Customer("customer", "customer@email.com", "password", "010-1234-5678",
				payAccountRepository.save(new PayAccount()), new NoOpPasswordEncoder()));
		store1 = storeRepository.saveAndFlush(
			new Store(vendor, storeCategory, "store", "송파", "010-1234-5678", 10000, LocalDate.now().atTime(9, 0),
				LocalDate.now().atTime(21, 0)));

		store2 = storeRepository.saveAndFlush(
			new Store(vendor, storeCategory, "store", "송파", "010-1234-5678", 10000, LocalDate.now().atTime(9, 0),
				LocalDate.now().atTime(21, 0)));
	}

	@Test
	@DisplayName("점주 주문 조회 테스트 - 성공")
	void testFindAllByOwner() {
		// given
		orderRepository.saveAndFlush(new Order(customer, store1));
		orderRepository.saveAndFlush(new Order(customer, store1));

		// when
		List<Order> orders = orderRepository.findAllByOwner(vendor.getId());

		// then
		assertEquals(2, orders.size());
	}

	@Test
	@DisplayName("점주 주문 조회 테스트 - 권한 없는 점주 실패")
	void testFindAllByOwnerFailWithUnauthorized() {
		// given
		orderRepository.saveAndFlush(new Order(customer, store1));

		// when
		List<Order> orders = orderRepository.findAllByOwner(differentVendor.getId());

		// then
		assertEquals(0, orders.size());
	}

	@Test
	@DisplayName("점주 특정 매장 주문 조회 테스트 - 성공")
	void testFindByStore() {
		// given
		Order order = orderRepository.saveAndFlush(new Order(customer, store1));
		orderRepository.saveAndFlush(new Order(customer, store2));

		// when
		List<Order> orders = orderRepository.findByStore(store1.getId(), vendor.getId());

		// then
		assertEquals(1, orders.size());
		assertEquals(order.getId(), orders.get(0).getId());
	}

	@Test
	@DisplayName("점주 특정 매장 주문 조회 테스트 - 권한 없는 점주 실패")
	void testFindByStoreFailWithUnauthorized() {
		// given
		orderRepository.saveAndFlush(new Order(customer, store1));

		// when
		List<Order> orders = orderRepository.findByStore(store1.getId(), differentVendor.getId());

		// then
		assertEquals(0, orders.size());
	}
}
