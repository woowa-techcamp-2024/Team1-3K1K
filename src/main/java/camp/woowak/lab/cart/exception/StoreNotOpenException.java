package camp.woowak.lab.cart.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class StoreNotOpenException extends BadRequestException {
	public StoreNotOpenException(String message) {
		super(CartErrorCode.STORE_NOT_OPEN, message);
	}
}
