package camp.woowak.lab.payment.exception;

import org.springframework.http.HttpStatus;

import camp.woowak.lab.common.exception.ErrorCode;

public enum OrderPaymentErrorCode implements ErrorCode {

	INVALID_SETTLEMENT_TARGET(HttpStatus.CONFLICT, "o_p_1", "OrderPayment 의 점주와 정산 대상 점주가 일치하지 않습니다."),
	INVALID_ORDER_PAYMENT_STATUS(HttpStatus.CONFLICT, "o_p_2", "OrderPayment 가 이미 정산된 상태입니다.");

	private final int status;
	private final String errorCode;
	private final String message;

	OrderPaymentErrorCode(HttpStatus status, String errorCode, String message) {
		this.status = status.value();
		this.errorCode = errorCode;
		this.message = message;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
