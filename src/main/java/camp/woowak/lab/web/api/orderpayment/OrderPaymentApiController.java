package camp.woowak.lab.web.api.orderpayment;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.payment.service.OrderPaymentSettlementService;
import camp.woowak.lab.web.dto.response.orderpayment.OrderPaymentSettlementResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderPaymentApiController {

	private final OrderPaymentSettlementService orderPaymentSettlementService;

	@PostMapping("/orderPayments/settlement")
	public OrderPaymentSettlementResponse settlementToAllVendors() {
		orderPaymentSettlementService.settleToAllVendors();

		return new OrderPaymentSettlementResponse("모든 정산을 완료하였습니다.");
	}
}
