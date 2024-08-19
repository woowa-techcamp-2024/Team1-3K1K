package camp.woowak.lab.payment.domain;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.common.exception.ErrorCode;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.order.domain.PriceChecker;
import camp.woowak.lab.order.domain.SingleStoreOrderValidator;
import camp.woowak.lab.order.domain.StockRequester;
import camp.woowak.lab.order.domain.WithdrawPointService;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payment.exception.CantSettleOrderPayment;
import camp.woowak.lab.payment.exception.OrderPaymentErrorCode;
import camp.woowak.lab.store.TestStore;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreAddress;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class OrderPaymentTest {

	PasswordEncoder passwordEncoder = new NoOpPasswordEncoder();
	Customer customer;
	Vendor vendor;
	Store store;
	List<Menu> menus;
	List<OrderItem> orderItems;
	List<CartItem> cartItems;

	@Mock
	SingleStoreOrderValidator singleStoreOrderValidator;

	@Mock
	StockRequester stockRequester;

	@Mock
	PriceChecker priceChecker;

	@Mock
	WithdrawPointService withdrawPointService;

	@Nested
	@DisplayName("총 주문 금액을 계산하는 기능은")
	class CalculateOrderPriceTest {

		@Test
		@DisplayName("[Success] 주문 아이템들의 가격과 수량에 대한 총 가격을 계산해야 한다")
		void shouldCalculateTotalPriceCorrectly() {
			// given
			long order1Price = 10000L;
			int order1Quantity = 2;
			long order2Price = 15000L;
			int order2Quantity = 1;
			long order3Price = 5000L;
			int order3Quantity = 3;

			long expectedTotalPrice =
				order1Price * order1Quantity + order2Price * order2Quantity + order3Price * order3Quantity;

			customer = createCustomer(createPayAccount());
			vendor = createVendor(createPayAccount()); // Vendor 객체 생성 (필요한 정보 설정)
			store = createStore(vendor, 1L); // Store 객체 생성

			MenuCategory menuCategory = new MenuCategory(store, "메뉴카테고리");
			menus = List.of(
				new Menu(store, menuCategory, "메뉴1", 10000L, 50L, "image1"),
				new Menu(store, menuCategory, "메뉴2", 15000L, 50L, "image2"),
				new Menu(store, menuCategory, "메뉴3", 5000L, 50L, "image3")
			);

			orderItems = List.of(
				new OrderItem(1L, order1Price, order1Quantity),
				new OrderItem(2L, order2Price, order2Quantity),
				new OrderItem(3L, order3Price, order3Quantity)
			);

			cartItems = List.of(
				new CartItem(1L, store.getId(), order1Quantity),
				new CartItem(2L, store.getId(), order2Quantity),
				new CartItem(3L, store.getId(), order3Quantity)
			);

			given(singleStoreOrderValidator.check(cartItems)).willReturn(store);
			given(priceChecker.check(store, cartItems)).willReturn(orderItems);

			LocalDateTime now = LocalDateTime.now();
			Order order = new Order(customer, cartItems, singleStoreOrderValidator, stockRequester, priceChecker,
				withdrawPointService, now);

			OrderPayment orderPayment = new OrderPayment(order, customer, vendor, OrderPaymentStatus.ORDER_SUCCESS,
				now);

			// when
			Long price = orderPayment.calculateOrderPrice();

			// then
			assertThat(price).isEqualTo(expectedTotalPrice);
		}

		@Test
		@DisplayName("[Success] 주문 아이템이 없는 경우 0원을 반환해야 한다")
		void shouldReturnZeroForEmptyOrder() {
			// given
			customer = createCustomer(createPayAccount());
			vendor = createVendor(createPayAccount()); // Vendor 객체 생성 (필요한 정보 설정)
			store = createStore(vendor, 1L); // Store 객체 생성

			MenuCategory menuCategory = new MenuCategory(store, "메뉴카테고리");
			menus = List.of(
				new Menu(store, menuCategory, "메뉴1", 10000L, 50L, "image1"),
				new Menu(store, menuCategory, "메뉴2", 15000L, 50L, "image2"),
				new Menu(store, menuCategory, "메뉴3", 5000L, 50L, "image3")
			);

			// 주문 아이템이 없는 경우
			orderItems = List.of();
			cartItems = List.of();

			given(singleStoreOrderValidator.check(cartItems)).willReturn(store);
			given(priceChecker.check(store, cartItems)).willReturn(orderItems);

			LocalDateTime now = LocalDateTime.now();
			Order order = new Order(customer, cartItems, singleStoreOrderValidator, stockRequester, priceChecker,
				withdrawPointService, now);

			OrderPayment orderPayment = new OrderPayment(order, customer, vendor, OrderPaymentStatus.ORDER_SUCCESS,
				now);

			// when
			Long price = orderPayment.calculateOrderPrice();

			// then
			assertThat(price).isZero();
		}

	}

	@Nested
	@DisplayName("정산 가능여부를 검증하는 기능은")
	class ValidateAbleToSettleTest {

		@Test
		@DisplayName("[Success] 정산 대상 Vendor가 수령자이고 상태가 ORDER_SUCCESS이면 예외가 발생하지 않는다.")
		void test1() {
			// given
			customer = createCustomer(createPayAccount());
			Vendor recipientVendor = createVendor(createPayAccount());

			given(singleStoreOrderValidator.check(cartItems)).willReturn(store);
			given(priceChecker.check(store, cartItems)).willReturn(orderItems);

			LocalDateTime now = LocalDateTime.now();
			Order order = new Order(customer, cartItems, singleStoreOrderValidator, stockRequester, priceChecker,
				withdrawPointService, now);
			OrderPayment orderPayment = new OrderPayment(order, customer, recipientVendor,
				OrderPaymentStatus.ORDER_SUCCESS,
				now);
			// when & then
			orderPayment.validateAbleToSettle(recipientVendor);  // 예외 발생 안함
		}

		@Test
		@DisplayName("[Exception] 정산 대상 Vendor가 수령자가 아니면 IllegalArgumentException이 발생한다.")
		void test2() {
			// given
			Vendor recipientVendor = createVendor(createPayAccount());
			Vendor anotherVendor = createVendor(createPayAccount());
			customer = createCustomer(createPayAccount());

			given(singleStoreOrderValidator.check(cartItems)).willReturn(store);
			given(priceChecker.check(store, cartItems)).willReturn(orderItems);

			LocalDateTime now = LocalDateTime.now();
			Order order = new Order(customer, cartItems, singleStoreOrderValidator, stockRequester, priceChecker,
				withdrawPointService, now);
			OrderPayment orderPayment = new OrderPayment(order, customer, recipientVendor,
				OrderPaymentStatus.ORDER_SUCCESS, now);

			// when & then
			Throwable thrown = catchThrowable(() -> orderPayment.validateAbleToSettle(anotherVendor));
			assertExceptionAndErrorCode(thrown, OrderPaymentErrorCode.INVALID_SETTLEMENT_TARGET);
		}

		@Test
		@DisplayName("[Exception] OrderPayment 상태가 ORDER_SUCCESS가 아니면 IllegalArgumentException이 발생한다.")
		void test3() {
			// given
			Vendor recipientVendor = createVendor(createPayAccount());

			given(singleStoreOrderValidator.check(cartItems)).willReturn(store);
			given(priceChecker.check(store, cartItems)).willReturn(orderItems);

			LocalDateTime now = LocalDateTime.now();
			Order order = new Order(customer, cartItems, singleStoreOrderValidator, stockRequester, priceChecker,
				withdrawPointService, now);
			OrderPayment orderPayment = new OrderPayment(order, customer, recipientVendor,
				OrderPaymentStatus.SETTLEMENT_SUCCESS, now);

			// when & then
			Throwable thrown = catchThrowable(() -> orderPayment.validateAbleToSettle(recipientVendor));
			assertExceptionAndErrorCode(thrown, OrderPaymentErrorCode.INVALID_ORDER_PAYMENT_STATUS);
		}

		private void assertExceptionAndErrorCode(Throwable thrown, ErrorCode expected) {
			assertThat(thrown).isInstanceOf(CantSettleOrderPayment.class);
			CantSettleOrderPayment exception = (CantSettleOrderPayment)thrown;
			assertThat(exception.errorCode()).isEqualTo(expected);
		}

	}

	private PayAccount createPayAccount() {
		return new PayAccount();
	}

	private Customer createCustomer(PayAccount payAccount) {
		return new Customer("customerName", "customerEmail@example.com", "customerPassword", "010-0000-0000",
			payAccount,
			passwordEncoder);
	}

	private Vendor createVendor(PayAccount payAccount) {
		return new Vendor("customerName", "customerEmail@example.com", "customerPassword", "010-0000-0000",
			payAccount,
			passwordEncoder);
	}

	private Store createStore(Vendor owner, Long storeId) {
		LocalDateTime validStartDateFixture = LocalDateTime.of(2020, 1, 1, 1, 1);
		LocalDateTime validEndDateFixture = LocalDateTime.of(2020, 1, 1, 2, 1);
		String validNameFixture = "3K1K 가게";
		String validAddressFixture = StoreAddress.DEFAULT_DISTRICT;
		String validPhoneNumberFixture = "02-1234-5678";
		Integer validMinOrderPriceFixture = 5000;

		return new TestStore(storeId, owner, createStoreCategory(), validNameFixture, validAddressFixture,
			validPhoneNumberFixture,
			validMinOrderPriceFixture,
			validStartDateFixture, validEndDateFixture);
	}

	private StoreCategory createStoreCategory() {
		return new StoreCategory("양식");
	}

}