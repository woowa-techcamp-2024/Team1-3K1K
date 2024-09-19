package camp.woowak.lab.order.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.infra.date.DateTimeProvider;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.order.exception.MultiStoreOrderException;
import camp.woowak.lab.store.domain.Store;

class OrderTest {

	private SingleStoreOrderValidator singleStoreOrderValidator;
	private StockRequester stockRequester;
	private PriceChecker priceChecker;
	private WithdrawPointService withdrawPointService;
	private final DateTimeProvider fixedDateTime = () -> LocalDateTime.of(2024, 8, 18, 1, 30, 30);
	private OrderFactory orderFactory;

	@BeforeEach
	void setUp() {
		singleStoreOrderValidator = mock(SingleStoreOrderValidator.class);
		stockRequester = mock(StockRequester.class);
		priceChecker = mock(PriceChecker.class);
		withdrawPointService = mock(WithdrawPointService.class);

		orderFactory = new OrderFactory(singleStoreOrderValidator, stockRequester, priceChecker, withdrawPointService,
			fixedDateTime);
	}

	@Test
	void createOrder_ValidInputs_Success() {
		// Given
		Customer customer = mock(Customer.class);
		Store store = mock(Store.class);
		CartItem cartItem = mock(CartItem.class);
		List<CartItem> cartItems = List.of(cartItem);

		OrderItem orderItem = mock(OrderItem.class);
		List<OrderItem> orderItems = List.of(orderItem);

		// Mocking behavior
		when(singleStoreOrderValidator.check(cartItems)).thenReturn(store);
		when(stockRequester.request(cartItems)).thenReturn(cartItems);
		when(priceChecker.check(store, cartItems)).thenReturn(orderItems);
		when(withdrawPointService.withdraw(customer, orderItems)).thenReturn(orderItems);

		// When
		Order order = orderFactory.createOrder(customer, cartItems);

		// Then
		assertEquals(orderItems, order.getOrderItems());

		verify(singleStoreOrderValidator, times(1)).check(cartItems);
		verify(stockRequester, times(1)).request(cartItems);
		verify(priceChecker, times(1)).check(store, cartItems);
		verify(withdrawPointService, times(1)).withdraw(customer, orderItems);
	}

	@Test
	void createOrder_InvalidStore_ThrowsException() {
		// Given
		Customer customer = mock(Customer.class);
		Store store = mock(Store.class);
		CartItem cartItem = mock(CartItem.class);
		List<CartItem> cartItems = List.of(cartItem);

		// Mock behavior to throw exception
		doThrow(new MultiStoreOrderException("다른 가게의 메뉴를 같이 주문할 수 없습니다."))
			.when(singleStoreOrderValidator).check(cartItems);

		// When & Then
		MultiStoreOrderException exception = assertThrows(MultiStoreOrderException.class, () -> {
			orderFactory.createOrder(customer, cartItems);
		});

		assertEquals("다른 가게의 메뉴를 같이 주문할 수 없습니다.", exception.getMessage());
	}
}
