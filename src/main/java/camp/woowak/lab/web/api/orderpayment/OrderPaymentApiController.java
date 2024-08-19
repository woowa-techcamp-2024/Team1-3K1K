package camp.woowak.lab.web.api.orderpayment;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.payment.service.OrderPaymentAdjustmentService;
import camp.woowak.lab.web.dto.response.orderpayment.OrderPaymentAdjustmentResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderPaymentApiController {

	private final OrderPaymentAdjustmentService orderPaymentAdjustmentService;

	@PostMapping("/orderPayments/adjustment")
	public OrderPaymentAdjustmentResponse adjustment() {
		orderPaymentAdjustmentService.adjustment();

		return new OrderPaymentAdjustmentResponse("모든 정산을 완료하였습니다.");
	}
}
