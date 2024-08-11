package camp.woowak.lab.web.api.payaccount;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.payaccount.service.PayAccountDepositService;
import camp.woowak.lab.payaccount.service.command.AccountTransactionCommand;
import camp.woowak.lab.web.dto.request.payaccount.PayAccountChargeRequest;
import camp.woowak.lab.web.dto.response.payaccount.PayAccountChargeResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/account")
@Slf4j
public class PayAccountApiController {
	private final PayAccountDepositService payAccountDepositService;

	public PayAccountApiController(PayAccountDepositService payAccountDepositService) {
		this.payAccountDepositService = payAccountDepositService;
	}

	/**
	 * TODO 1. api end-point 설계 논의
	 * TODO 2. 인증 방법에 대한 유저 구분값 가져오는 방법 논의
	 * TODO 3. API Response Format에 대한 논의 후 적용 필요
	 */
	@PostMapping("/{payAccountId}/charge")
	public ResponseEntity<PayAccountChargeResponse> payAccountCharge(@PathVariable("payAccountId") Long payAccountId,
																	 @Validated @RequestBody PayAccountChargeRequest request) {
		AccountTransactionCommand command = new AccountTransactionCommand(payAccountId, request.amount());
		log.info("Pay account charge request received. Account ID: {}, Charge Amount: {}", payAccountId,
			request.amount());

		long remainBalance = payAccountDepositService.depositAccount(command);
		log.info("Charge successful. Account ID: {}, New Balance: {}", payAccountId, remainBalance);

		return ResponseEntity.ok(new PayAccountChargeResponse(remainBalance));
	}
}
