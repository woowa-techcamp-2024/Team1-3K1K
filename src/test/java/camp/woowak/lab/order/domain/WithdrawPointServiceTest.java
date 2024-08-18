package camp.woowak.lab.order.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.exception.NotFoundAccountException;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;

class WithdrawPointServiceTest {

	private WithdrawPointService withdrawPointService;
	private PayAccountRepository payAccountRepository;

	@BeforeEach
	void setUp() {
		payAccountRepository = mock(PayAccountRepository.class);
		withdrawPointService = new WithdrawPointService(payAccountRepository);
	}

	@Test
	void withdraw_NoPayAccount_ThrowsNotFoundAccountException() {
		// Given
		Customer customer = mock(Customer.class);
		UUID customerId = UUID.randomUUID();
		when(customer.getId()).thenReturn(customerId);

		when(payAccountRepository.findByCustomerIdForUpdate(customerId))
			.thenReturn(Optional.empty());

		// When & Then
		assertThrows(NotFoundAccountException.class, () -> withdrawPointService.withdraw(customer, List.of()));
	}

	@Test
	void withdraw_ValidPayAccount_WithdrawsPointsSuccessfully() {
		// Given
		Customer customer = mock(Customer.class);
		UUID customerId = UUID.randomUUID();
		when(customer.getId()).thenReturn(customerId);

		PayAccount payAccount = mock(PayAccount.class);
		when(payAccountRepository.findByCustomerIdForUpdate(customerId))
			.thenReturn(Optional.of(payAccount));

		OrderItem orderItem1 = mock(OrderItem.class);
		OrderItem orderItem2 = mock(OrderItem.class);
		when(orderItem1.getTotalPrice()).thenReturn(500L);
		when(orderItem2.getTotalPrice()).thenReturn(1500L);

		List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

		// When
		List<OrderItem> result = withdrawPointService.withdraw(customer, orderItems);

		// Then
		verify(payAccount).withdraw(2000); // 500 + 1500
		assertEquals(orderItems, result);
	}
}
