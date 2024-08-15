package camp.woowak.lab.cart.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class OtherStoreMenuException extends BadRequestException {
	public OtherStoreMenuException(String message) {
		super(CartErrorCode.OTHER_STORE_MENU, message);
	}
}
