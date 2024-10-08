package camp.woowak.lab.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.cart.repository.CartRepository;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.infra.date.DateTimeProvider;
import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.order.domain.PriceChecker;
import camp.woowak.lab.order.domain.SingleStoreOrderValidator;
import camp.woowak.lab.order.domain.StockRequester;
import camp.woowak.lab.order.domain.WithdrawPointService;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.order.exception.EmptyCartException;
import camp.woowak.lab.order.repository.OrderRepository;
import camp.woowak.lab.order.service.command.OrderCreationCommand;
import camp.woowak.lab.payment.repository.OrderPaymentRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.repository.StoreRepository;

@SpringBootTest
class OrderCreationServiceTest {

	@Mock
	private OrderRepository orderRepository;
	@Mock
	private CartRepository cartRepository;
	@Mock
	private StoreRepository storeRepository;
	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private SingleStoreOrderValidator singleStoreOrderValidator;
	@Mock
	private StockRequester stockRequester;
	@Mock
	private WithdrawPointService withdrawPointService;
	@Mock
	private PriceChecker priceChecker;
	@Mock
	private OrderPaymentRepository orderPaymentRepository;

	@InjectMocks
	private OrderCreationService orderCreationService;

	private final DateTimeProvider fixedDateTime = () -> LocalDateTime.of(2024, 8, 18, 1, 30, 30);

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createOrder_Success() {
		// Given
		Customer customer = mock(Customer.class);
		Store store = mock(Store.class);
		CartItem cartItem = mock(CartItem.class);
		List<CartItem> cartItems = List.of(cartItem);

		// Prepare OrderItems to return from priceChecker
		OrderItem orderItem = mock(OrderItem.class);
		List<OrderItem> orderItems = List.of(orderItem);

		// Mocking behavior
		when(store.getId()).thenReturn(1L); // Ensure this is mocked
		when(cartItem.getStoreId()).thenReturn(1L); // Mock the cartItem's storeId
		when(singleStoreOrderValidator.check(anyList())).thenReturn(store);
		when(stockRequester.request(anyList())).thenReturn(cartItems);
		when(priceChecker.check(any(), anyList())).thenReturn(orderItems);
		when(withdrawPointService.withdraw(any(Customer.class), anyList())).thenReturn(orderItems);

		// When
		Order order = new Order(customer, cartItems, singleStoreOrderValidator, stockRequester, priceChecker,
			withdrawPointService, fixedDateTime.now());

		// Then
		assertEquals(orderItems, order.getOrderItems());

		verify(singleStoreOrderValidator, times(1)).check(cartItems);
		verify(stockRequester, times(1)).request(cartItems);
		verify(priceChecker, times(1)).check(store, cartItems);
		verify(withdrawPointService, times(1)).withdraw(customer, orderItems);
	}

	@Test
	void createOrder_EmptyCart_ThrowsException() {
		// Given
		UUID customerId = UUID.randomUUID();
		OrderCreationCommand command = new OrderCreationCommand(customerId);
		Customer customer = mock(Customer.class);

		when(customerRepository.findByIdOrThrow(customerId)).thenReturn(customer);
		when(cartRepository.findByCustomerId(customerId.toString())).thenReturn(Optional.empty());

		// When & Then
		EmptyCartException exception = assertThrows(EmptyCartException.class,
			() -> orderCreationService.create(command));

		assertEquals("구매자 " + customerId + "가 비어있는 카트로 주문을 시도했습니다.", exception.getMessage());
	}
}
