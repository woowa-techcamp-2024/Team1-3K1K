package camp.woowak.lab.menu.exception;

import camp.woowak.lab.common.exception.ConflictException;

public class InvalidMenuStockUpdateException extends ConflictException {
	public InvalidMenuStockUpdateException(String message) {
		super(MenuErrorCode.INVALID_UPDATE_MENU_STOCK, message);
	}
}
