package camp.woowak.lab.menu.exception;

import camp.woowak.lab.common.exception.ConflictException;

public class NotUpdatableTimeException extends ConflictException {
	public NotUpdatableTimeException(String message) {
		super(MenuErrorCode.NOT_UPDATABLE_TIME, message);
	}
}
