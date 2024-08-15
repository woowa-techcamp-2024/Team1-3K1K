package camp.woowak.lab.cart.exception;

import camp.woowak.lab.common.exception.NotFoundException;

public class MenuNotFoundException extends NotFoundException {
	public MenuNotFoundException(String message) {
		super(CartErrorCode.MENU_NOT_FOUND, message);
	}
}
