package camp.woowak.lab.customer.exception;

import camp.woowak.lab.common.exception.ErrorCode;

public enum CustomerErrorCode implements ErrorCode {
	INVALID_CREATION(400, "C1", "잘못된 요청입니다."),
	DUPLICATE_EMAIL(400, "C2", "이미 존재하는 이메일입니다.");

	private final int status;
	private final String errorCode;
	private final String message;

	CustomerErrorCode(int status, String errorCode, String message) {
		this.status = status;
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
