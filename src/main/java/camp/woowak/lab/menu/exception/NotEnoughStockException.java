package camp.woowak.lab.menu.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class NotEnoughStockException extends BadRequestException {
	public NotEnoughStockException(String message) {
		super(MenuErrorCode.NOT_ENOUGH_STOCK, message);
	}
}
