package camp.woowak.lab.store.exception;

import static camp.woowak.lab.store.exception.StoreErrorCode.*;

import camp.woowak.lab.common.exception.BadRequestException;

public class NotEqualsOwnerException extends BadRequestException {

	public NotEqualsOwnerException(String message) {
		super(NOT_EQUALS_VENDOR, message);
	}

}
