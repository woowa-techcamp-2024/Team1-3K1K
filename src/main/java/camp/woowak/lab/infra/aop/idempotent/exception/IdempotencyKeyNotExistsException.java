package camp.woowak.lab.infra.aop.idempotent.exception;

import camp.woowak.lab.common.exception.UnauthorizedException;

public class IdempotencyKeyNotExistsException extends UnauthorizedException {
	public IdempotencyKeyNotExistsException(String message) {
		super(IdempotencyKeyErrorCode.IDEMPOTENCY_KEY_ERROR_CODE, message);
	}
}
