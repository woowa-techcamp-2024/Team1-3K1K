package camp.woowak.lab.infra.aop.idempotent.exception;

import org.springframework.http.HttpStatus;

import camp.woowak.lab.common.exception.ErrorCode;

public enum IdempotencyKeyErrorCode implements ErrorCode {
	IDEMPOTENCY_KEY_ERROR_CODE(HttpStatus.UNAUTHORIZED, "idem1", "인증 키가 필요합니다.");

	private final HttpStatus status;
	private final String errorCode;
	private final String message;

	IdempotencyKeyErrorCode(HttpStatus status, String errorCode, String message) {
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
