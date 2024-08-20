package camp.woowak.lab.menu.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class NotEqualsOwnerException extends BadRequestException {
	public NotEqualsOwnerException(String message) {
		super(MenuErrorCode.NOT_EQUALS_OWNER, message);
	}
}
