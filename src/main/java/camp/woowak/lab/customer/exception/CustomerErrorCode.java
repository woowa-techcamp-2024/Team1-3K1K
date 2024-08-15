package camp.woowak.lab.customer.exception;

import org.springframework.http.HttpStatus;

import camp.woowak.lab.common.exception.ErrorCode;

public enum CustomerErrorCode implements ErrorCode {
	INVALID_CREATION(HttpStatus.BAD_REQUEST, "C1", "잘못된 요청입니다."),
	DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "C2", "이미 존재하는 이메일입니다."),
	AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "C3", "이메일 또는 비밀번호가 올바르지 않습니다.");

	private final HttpStatus status;
	private final String errorCode;
	private final String message;

	CustomerErrorCode(HttpStatus status, String errorCode, String message) {
		this.status = status;
		this.errorCode = errorCode;
		this.message = message;
	}

	@Override
	public int getStatus() {
		return status.value();
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
