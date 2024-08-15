package camp.woowak.lab.store.exception;

import camp.woowak.lab.common.exception.NotFoundException;

public class NotFoundStoreCategoryException extends NotFoundException {

	public NotFoundStoreCategoryException(String message) {
		super(StoreErrorCode.INVALID_STORE_CATEGORY, message);
	}

}
