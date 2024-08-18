package camp.woowak.lab.menu.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class InvalidMenuPriceUpdateException extends BadRequestException {
	public InvalidMenuPriceUpdateException(String message) {
		super(MenuErrorCode.INVALID_PRICE, message);
	}
}
