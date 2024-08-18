package camp.woowak.lab.menu.exception;

import camp.woowak.lab.common.exception.ConflictException;

public class StoreNotOpenException extends ConflictException {
	public StoreNotOpenException(String message) {
		super(MenuErrorCode.NOT_OPEN_STORE, message);
	}
}
