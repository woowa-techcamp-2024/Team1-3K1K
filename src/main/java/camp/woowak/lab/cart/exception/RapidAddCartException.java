package camp.woowak.lab.cart.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class RapidAddCartException extends BadRequestException {
	public RapidAddCartException(String message) {
		super(CartErrorCode.RAPID_ADD_MENU, message);
	}
}
