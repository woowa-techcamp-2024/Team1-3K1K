package camp.woowak.lab.payaccount.exception;

import org.springframework.http.HttpStatus;

import camp.woowak.lab.common.exception.ErrorCode;

public enum PayAccountErrorCode implements ErrorCode {
	INVALID_TRANSACTION_AMOUNT(HttpStatus.BAD_REQUEST, "a_1_1", "금액은 0보다 커야합니다."),
	DAILY_LIMIT_EXCEED(HttpStatus.BAD_REQUEST, "a_1_2", "일일 충전 한도 금액을 초과했습니다."),
	INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST,"a_1_3","금액이 부족합니다."),
	ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "a_1_4", "계좌를 찾을 수 없습니다.");

	private final int status;
	private final String errorCode;
	private final String message;

	PayAccountErrorCode(HttpStatus status, String errorCode, String message) {
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
