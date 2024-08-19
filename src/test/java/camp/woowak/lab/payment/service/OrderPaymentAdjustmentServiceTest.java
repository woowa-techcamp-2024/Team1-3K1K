package camp.woowak.lab.payment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payment.domain.OrderPayment;
import camp.woowak.lab.payment.domain.OrderPaymentStatus;
import camp.woowak.lab.payment.repository.OrderPaymentRepository;
import camp.woowak.lab.vendor.TestVendor;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.exception.NotFoundVendorException;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class OrderPaymentAdjustmentServiceTest {

	@Mock
	private VendorRepository vendorRepository;

	@Mock
	private OrderPaymentRepository orderPaymentRepository;

	@Mock
	private AdjustmentCalculator adjustmentCalculator;

	@InjectMocks
	private OrderPaymentAdjustmentService orderPaymentAdjustmentService;

	private static PasswordEncoder passwordEncoder = new NoOpPasswordEncoder();

	private Vendor vendor1;
	private Vendor vendor2;

	@Nested
	@DisplayName("주문 금액을 정산하는 기능은")
	class AdjustmentTest {

		@BeforeEach
		void setup() {
			PayAccount payAccount1 = new PayAccount(); // PayAccount 클래스 구현 필요
			PayAccount payAccount2 = new PayAccount();

			UUID vendor1Id = UUID.randomUUID();
			UUID vendor2Id = UUID.randomUUID();
			vendor1 = new TestVendor(vendor1Id, "Vendor1", "vendor1@test.com", "password", "1234567890", payAccount1,
				passwordEncoder);
			vendor2 = new TestVendor(vendor2Id, "Vendor2", "vendor2@test.com", "password", "0987654321", payAccount2,
				passwordEncoder);
		}

		@Test
		@DisplayName("[Success] 정산에 성공하면 정산금액만큼 점주에게 송금된다.")
		void adjustment_ShouldProcessAllVendorsAndUpdateOrderPayments() {
			// given
			OrderPayment orderPayment1 = mock(OrderPayment.class);
			OrderPayment orderPayment2 = mock(OrderPayment.class);

			// Mocking behavior 추가
			doNothing().when(orderPayment1).validateReadyToAdjustment(any(Vendor.class));
			doNothing().when(orderPayment2).validateReadyToAdjustment(any(Vendor.class));

			List<Vendor> vendors = List.of(vendor1, vendor2);
			List<OrderPayment> payments = List.of(orderPayment1, orderPayment2);
			List<OrderPayment> paymentsVendor1 = List.of(orderPayment1, orderPayment2);
			List<OrderPayment> paymentsVendor2 = List.of(); // 빈 리스트

			given(vendorRepository.findAll()).willReturn(vendors);
			given(orderPaymentRepository.findByRecipientIdAndOrderPaymentStatus(vendor1.getId(),
				OrderPaymentStatus.ORDER_SUCCESS))
				.willReturn(payments);
			given(orderPaymentRepository.findByRecipientIdAndOrderPaymentStatus(vendor2.getId(),
				OrderPaymentStatus.ORDER_SUCCESS))
				.willReturn(List.of());

			// vendor1의 payments에 대해서는 정산금액 2000L을 반환하고, vendor2에게는 정산금액 1000L을 반환
			given(adjustmentCalculator.calculate(paymentsVendor1)).willReturn(2000L);
			given(adjustmentCalculator.calculate(paymentsVendor2)).willReturn(1000L);

			// When
			orderPaymentAdjustmentService.adjustment();

			// Then
			then(vendorRepository).should().findAll();
			then(orderPaymentRepository).should()
				.findByRecipientIdAndOrderPaymentStatus(vendor1.getId(), OrderPaymentStatus.ORDER_SUCCESS);
			then(adjustmentCalculator).should().calculate(payments);
			then(orderPaymentRepository).should()
				.updateOrderPaymentStatus(List.of(orderPayment1.getId(), orderPayment2.getId()),
					OrderPaymentStatus.ADJUSTMENT_SUCCESS);

			assertThat(vendor1.getPayAccount().getBalance()).isEqualTo(2000L);
			assertThat(vendor2.getPayAccount().getBalance()).isEqualTo(1000L);
		}

		@Test
		@DisplayName("[Exception] 정산도중 예외가 발생하면 점주의 원래 금액에는 변화가 없다.")
		void adjustment_ShouldThrowExceptionWhenVendorNotFound() {
			// given
			PayAccount payAccount1 = new PayAccount();
			payAccount1.charge(5000L); // 기존 금액 설정
			Vendor vendor1 = new Vendor("Vendor1", "vendor1@test.com", "password", "1234567890", payAccount1,
				passwordEncoder);
			List<Vendor> vendors = List.of(vendor1);

			given(vendorRepository.findAll()).willReturn(vendors);
			// 특정 조건에서 예외 발생시키기
			given(orderPaymentRepository.findByRecipientIdAndOrderPaymentStatus(vendor1.getId(),
				OrderPaymentStatus.ORDER_SUCCESS))
				.willThrow(new NotFoundVendorException());

			// When & Then
			assertThatThrownBy(() -> orderPaymentAdjustmentService.adjustment())
				.isInstanceOf(NotFoundVendorException.class);

			// 예외 발생 후에도 기존 금액이 유지되는지 확인
			assertThat(vendor1.getPayAccount().getBalance()).isEqualTo(5000L);
		}
	}

}