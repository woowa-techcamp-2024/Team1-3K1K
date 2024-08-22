package camp.woowak.lab.cart.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class NotFoundCartException extends BadRequestException {
	public NotFoundCartException(String message) {
		super(CartErrorCode.NOT_FOUND, message);
	}
}
