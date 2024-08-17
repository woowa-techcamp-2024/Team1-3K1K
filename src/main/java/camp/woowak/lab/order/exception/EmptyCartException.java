package camp.woowak.lab.order.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class EmptyCartException extends BadRequestException {
	public EmptyCartException(String message) {
		super(OrderErrorCode.EMPTY_CART, message);
	}
}
