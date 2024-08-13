package camp.woowak.lab.web.authentication;

import org.springframework.http.HttpStatus;

import camp.woowak.lab.common.exception.ErrorCode;

public enum AuthenticationErrorCode implements ErrorCode {
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "a1", "로그인이 필요합니다.");

	private final int status;
	private final String errorCode;
	private final String message;

	AuthenticationErrorCode(HttpStatus httpStatus, String errorCode, String message) {
		this.status = httpStatus.value();
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
