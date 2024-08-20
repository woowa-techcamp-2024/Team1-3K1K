package camp.woowak.lab.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.repository.CartRepository;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.menu.repository.MenuCategoryRepository;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.order.exception.EmptyCartException;
import camp.woowak.lab.order.repository.OrderRepository;
import camp.woowak.lab.order.service.command.OrderCreationCommand;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.payment.domain.OrderPayment;
import camp.woowak.lab.payment.domain.OrderPaymentStatus;
import camp.woowak.lab.payment.repository.OrderPaymentRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreAddress;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@SpringBootTest
public class OrderPaymentCreateTest {

	@Autowired
	OrderCreationService orderCreationService;

	@Autowired
	OrderPaymentRepository orderPaymentRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	VendorRepository vendorRepository;

	@Autowired
	StoreRepository storeRepository;

	@Autowired
	PayAccountRepository payAccountRepository;

	@Autowired
	StoreCategoryRepository storeCategoryRepository;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	MenuCategoryRepository menuCategoryRepository;

	@Autowired
	MenuRepository menuRepository;

	@Autowired
	OrderRepository orderRepository;

	@Nested
	@DisplayName("OrderPayment 를 저장하는 기능은")
	class SaveOrderPaymentTest {

		PayAccount customerPayAccount;
		Customer customer;
		PayAccount vendorPayAccount;
		Vendor vendor;
		StoreCategory storeCategory;
		Store store;
		MenuCategory menuCategory;
		Menu menu;
		Cart cart;

		@Test
		@DisplayName("[Success] 주문을 성공하면 OrderPayment 상태가 ORDER_SUCCESS 인 OrderPayment 를 저장한다.")
		void test1() {
			// given
			customerPayAccount = setupPayAccount();
			customer = setupCustomer(customerPayAccount, "customer1@naver.com");
			vendorPayAccount = setupPayAccount();
			vendor = setupVendor(vendorPayAccount, "vendor1@naver.com");
			storeCategory = setupStoreCategory();
			store = setupStore(vendor, storeCategory);
			menuCategory = setupMenuCategory(store);
			menu = setupMenu(store, menuCategory);
			cart = setupCart(customer, menu);

			OrderCreationCommand command = new OrderCreationCommand(customer.getId());

			// when
			Long orderId = orderCreationService.create(command);

			// then
			List<OrderPayment> orderPaymentStatuses = orderPaymentRepository.findByRecipientIdAndOrderPaymentStatus(
				vendor.getId(),
				OrderPaymentStatus.ORDER_SUCCESS);
			OrderPayment orderPayment = orderPaymentStatuses.get(0);
			assertThat(orderPayment.getOrder().getId()).isEqualTo(orderId);
			assertThat(orderPayment.getOrderPaymentStatus()).isEqualTo(OrderPaymentStatus.ORDER_SUCCESS);
		}

		@Test
		@DisplayName("[Exception] 주문을 실패하면 OrderPayment 도 저장되지 않는다.")
		void test2() {
			// given
			vendorPayAccount = setupPayAccount();
			vendor = setupVendor(vendorPayAccount, "vendor2@naver.com");

			customerPayAccount = setupPayAccount();
			customer = setupCustomer(customerPayAccount, "customer2@naver.com");

			// 이 시나리오에서는, 고객이 카트에 아무것도 추가하지 않았다고 가정
			cart = new Cart(customer.getId().toString());

			OrderCreationCommand command = new OrderCreationCommand(customer.getId());

			// when & then
			assertThrows(EmptyCartException.class, () -> {
				orderCreationService.create(command);
			});

			// OrderPayment 가 저장되지 않았는지 확인
			List<OrderPayment> orderPayments = orderPaymentRepository.findByRecipientIdAndOrderPaymentStatus(
				vendor.getId(), OrderPaymentStatus.ORDER_SUCCESS);
			assertThat(orderPayments).isEmpty();
		}

		private Cart setupCart(Customer customer, Menu menu) {
			Cart cart = new Cart(customer.getId().toString());
			cart.addMenu(menu);
			cartRepository.save(cart);
			return cart;
		}

		private Menu setupMenu(Store store, MenuCategory menuCategory) {
			Menu menu = new Menu(store, menuCategory, "메뉴", 5000L, 10L, "image");
			menuRepository.saveAndFlush(menu);
			return menu;
		}

		private MenuCategory setupMenuCategory(Store store) {
			MenuCategory menuCategory = new MenuCategory(store, "메뉴카테고리");
			menuCategoryRepository.saveAndFlush(menuCategory);
			return menuCategory;
		}

		private Store setupStore(Vendor vendor, StoreCategory storeCategory) {
			LocalDateTime openingTime = LocalDateTime.now().minusMinutes(10).withSecond(0).withNano(0);
			LocalDateTime closingTime = LocalDateTime.now().plusMinutes(40).withSecond(0).withNano(0);
			Store store = createValidStore(vendor, storeCategory, openingTime, closingTime);
			storeRepository.saveAndFlush(store);
			return store;
		}

		private StoreCategory setupStoreCategory() {
			StoreCategory storeCategory = createStoreCategory();
			storeCategoryRepository.saveAndFlush(storeCategory);
			return storeCategory;
		}

		private Vendor setupVendor(PayAccount vendorPayAccount, String email) {
			Vendor vendor = createVendor(vendorPayAccount, email);
			vendorRepository.saveAndFlush(vendor);
			return vendor;
		}

		private Customer setupCustomer(PayAccount customerPayAccount, String email) {
			Customer customer = createCustomer(customerPayAccount, email);
			customerRepository.saveAndFlush(customer);
			return customer;
		}

		private PayAccount setupPayAccount() {
			PayAccount payAccount = new PayAccount();
			payAccount.charge(100000L);
			payAccountRepository.saveAndFlush(payAccount);
			return payAccount;
		}

		private Vendor createVendor(PayAccount payAccount, String email) {
			PasswordEncoder passwordEncoder = new NoOpPasswordEncoder();
			return new Vendor("vendorName", email, "vendorPassword", "010-0000-0000", payAccount,
				passwordEncoder);
		}

		private Customer createCustomer(PayAccount payAccount, String email) {
			PasswordEncoder passwordEncoder = new NoOpPasswordEncoder();
			return new Customer(
				"customerName",
				email,
				"customerPassword",
				"010-1234-5678",
				payAccount,
				passwordEncoder
			);
		}

		private Store createValidStore(Vendor owner, StoreCategory storeCategory,
									   LocalDateTime validStartDateFixture,
									   LocalDateTime validEndDateFixture) {
			String validNameFixture = "3K1K 가게";
			String validAddressFixture = StoreAddress.DEFAULT_DISTRICT;
			String validPhoneNumberFixture = "02-1234-5678";
			Integer validMinOrderPriceFixture = 5000;

			return new Store(owner, storeCategory, validNameFixture, validAddressFixture,
				validPhoneNumberFixture,
				validMinOrderPriceFixture,
				validStartDateFixture, validEndDateFixture);
		}

		private StoreCategory createStoreCategory() {
			return new StoreCategory("양식");
		}

	}

}
