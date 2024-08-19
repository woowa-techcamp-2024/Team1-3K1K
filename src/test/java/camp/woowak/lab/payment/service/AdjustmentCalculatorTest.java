package camp.woowak.lab.payment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import camp.woowak.lab.payment.domain.OrderPayment;

class AdjustmentCalculatorTest {

	private AdjustmentCalculator adjustmentCalculator;

	@Mock
	private OrderPayment orderPayment1;

	@Mock
	private OrderPayment orderPayment2;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		adjustmentCalculator = new AdjustmentCalculator();
	}

	@Test
	void calculate_shouldReturnCorrectAmount() {
		// Given
		when(orderPayment1.calculateOrderPrice()).thenReturn(10000L);
		when(orderPayment2.calculateOrderPrice()).thenReturn(20000L);
		List<OrderPayment> orderPayments = Arrays.asList(orderPayment1, orderPayment2);

		// When
		Long result = adjustmentCalculator.calculate(orderPayments);

		// Then
		Long expectedTotalPrice = 30000L;
		Long expectedCommission = 1500L; // 5% of 30000
		Long expectedResult = 28500L; // 30000 - 1500

		assertThat(result).isEqualTo(expectedResult);
	}

	@Test
	void calculate_withZeroOrderPrice_shouldReturnZero() {
		// Given
		when(orderPayment1.calculateOrderPrice()).thenReturn(0L);
		List<OrderPayment> orderPayments = Collections.singletonList(orderPayment1);

		// When
		Long result = adjustmentCalculator.calculate(orderPayments);

		// Then
		assertThat(result).isZero();
	}

	@Test
	void calculate_withLargeOrderPrice_shouldReturnCorrectAmount() {
		// Given
		when(orderPayment1.calculateOrderPrice()).thenReturn(1000000000L); // 10억원
		List<OrderPayment> orderPayments = Collections.singletonList(orderPayment1);

		// When
		Long result = adjustmentCalculator.calculate(orderPayments);

		// Then
		Long expectedCommission = 50000000L; // 5% of 10억원
		Long expectedResult = 950000000L; // 10억원 - 5000만원

		assertThat(result).isEqualTo(expectedResult);
	}

	@Test
	void calculate_withEmptyOrderPayments_shouldReturnZero() {
		// Given
		List<OrderPayment> orderPayments = Collections.emptyList();

		// When
		Long result = adjustmentCalculator.calculate(orderPayments);

		// Then
		assertThat(result).isZero();
	}

	@Test
	void calculate_withFractionalAmount_shouldRoundDown() {
		// Given
		when(orderPayment1.calculateOrderPrice()).thenReturn(10001L);
		List<OrderPayment> orderPayments = Collections.singletonList(orderPayment1);

		// When
		Long result = adjustmentCalculator.calculate(orderPayments);

		// Then
		Long expectedCommission = 500L; // 5% of 10001 = 500.05, but it's rounded down to 500
		Long expectedResult = 9501L; // 10001 - 500

		assertThat(result).isEqualTo(expectedResult);
	}

	@Test
	void calculate_withMultipleFractionalAmounts_shouldRoundDownTotal() {
		// Given
		when(orderPayment1.calculateOrderPrice()).thenReturn(10001L);
		when(orderPayment2.calculateOrderPrice()).thenReturn(20002L);
		List<OrderPayment> orderPayments = Arrays.asList(orderPayment1, orderPayment2);

		// When
		Long result = adjustmentCalculator.calculate(orderPayments);

		// Then
		Long expectedTotalPrice = 30003L;
		Long expectedCommission = 1500L; // 5% of 30003 = 1500.15, but it's rounded down to 1500
		Long expectedResult = 28503L; // 30003 - 1500

		assertThat(result).isEqualTo(expectedResult);
	}

	@Test
	void calculate_withLargeAmount_shouldReturnCorrectResult() {
		// Given
		long largeAmount = Long.MAX_VALUE;
		when(orderPayment1.calculateOrderPrice()).thenReturn(largeAmount);
		List<OrderPayment> orderPayments = Collections.singletonList(orderPayment1);

		// When
		long result = adjustmentCalculator.calculate(orderPayments);

		// Then
		long expectedCommission = (long)(largeAmount * 0.05);
		long expectedResult = largeAmount - expectedCommission;

		assertThat(result).isEqualTo(expectedResult);
	}

}