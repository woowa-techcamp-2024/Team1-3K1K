package camp.woowak.lab.customer.exception;

import camp.woowak.lab.common.exception.ErrorCode;

public enum CustomerErrorCode implements ErrorCode {
	INVALID_PAY_ACCOUNT_IS_NOT_NULL(400, "C1", "Customer payAccount cannot be null"),
	INVALID_PHONE_IS_NOT_BLANK(400, "C2", "Customer phone cannot be blank"),
	INVALID_PHONE_IS_TOO_LONG(400, "C3", "Customer phone cannot be longer than 30 characters"),
	INVALID_PASSWORD_IS_NOT_BLANK(400, "C4", "Customer password cannot be blank"),
	INVALID_PASSWORD_IS_TOO_LONG(400, "C5", "Customer password cannot be longer than 30 characters"),
	INVALID_EMAIL_IS_NOT_BLANK(400, "C6", "Customer email cannot be blank"),
	INVALID_EMAIL_IS_TOO_LONG(400, "C7", "Customer email cannot be longer than 100 characters"),
	INVALID_NAME_IS_NOT_BLANK(400, "C8", "Customer name cannot be blank"),
	INVALID_NAME_IS_TOO_LONG(400, "C9", "Customer name cannot be longer than 100 characters"),
	DUPLICATE_EMAIL(400, "C10", "Customer email is already in use");

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
