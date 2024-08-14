package camp.woowak.lab.cart.exception;

import org.springframework.http.HttpStatus;

import camp.woowak.lab.common.exception.ErrorCode;

public enum CartErrorCode implements ErrorCode {
	;

	private final int status;
	private final String errorCode;
	private final String message;

	CartErrorCode(HttpStatus status, String errorCode, String message) {
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
