package camp.woowak.lab.store.exception;

import static camp.woowak.lab.store.exception.StoreErrorCode.*;

import camp.woowak.lab.common.exception.NotFoundException;

public class NotFoundStoreException extends NotFoundException {

	public NotFoundStoreException(String message) {
		super(NOT_FOUND_STORE, message);
	}

}
