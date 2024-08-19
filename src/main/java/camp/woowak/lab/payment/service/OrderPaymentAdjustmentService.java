package camp.woowak.lab.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.payment.domain.OrderPayment;
import camp.woowak.lab.payment.domain.OrderPaymentStatus;
import camp.woowak.lab.payment.repository.OrderPaymentRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.exception.NotFoundVendorException;
import camp.woowak.lab.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;

/**
 * 정산을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class OrderPaymentAdjustmentService {

	private final VendorRepository vendorRepository;
	private final MenuRepository menuRepository;
	private final OrderPaymentRepository orderPaymentRepository;

	/**
	 * @throws NotFoundVendorException vendorId에 해당하는 점주를 찾을 수 없을 떄
	 */
	@Transactional
	public void adjustment() {
		// 1. 모든 점주 조회
		List<Vendor> vendors = findAllVendors();
		for (Vendor vendor : vendors) {
			// 2. 각 점주의 정산해야할 OrderPayment 목록을 조회
			List<OrderPayment> orderPayments = findOrderPaymentsToAdjustment(vendor);

			// 3. 각 OrderPayment 의 Order 주문 금액을 계산
			Long totalOrderPrice = calculateTotalOrderPrice(orderPayments);

			// 4. 총 주문 금액에서 수수료 5%를 계산
			Double commission = calculateCommission(totalOrderPrice);

			// 5. 수수료를 제외한 금액을 점주에게 송금

			// 6. 송금을 성공하면, OrderPayment 목록의 OrderPaymentStatus 상태를 ADJUSTMENT_SUCCESS 로 갱신
			updateOrderPaymentStatus(orderPayments);
		}
	}

	// 모든 점주 조회
	private List<Vendor> findAllVendors() {
		return vendorRepository.findAll();
	}

	// 각 점주의 정산해야할 OrderPayment 목록을 조회
	private List<OrderPayment> findOrderPaymentsToAdjustment(final Vendor vendor) {
		List<OrderPayment> orderPayments = orderPaymentRepository.findByRecipientIdAndOrderPaymentStatus(
			vendor.getId(), OrderPaymentStatus.ORDER_SUCCESS);

		for (OrderPayment orderPayment : orderPayments) {
			// 정산해야할 OrderPayment 가 맞는지 검증
			orderPayment.validateReadyToAdjustment(vendor);
		}

		return orderPayments;
	}

	// OrderPayment 목록의 OrderPaymentStatus 상태를 ADJUSTMENT_SUCCESS 로 갱신
	private void updateOrderPaymentStatus(List<OrderPayment> orderPayments) {
		orderPaymentRepository.updateOrderPaymentStatus(
			orderPayments.stream().map(OrderPayment::getId).toList(),
			OrderPaymentStatus.ADJUSTMENT_SUCCESS);
	}

}
