package camp.woowak.lab.store.exception;

import camp.woowak.lab.common.exception.BadRequestException;
import camp.woowak.lab.common.exception.ErrorCode;

public class InvalidStoreCreationException extends BadRequestException {

	public InvalidStoreCreationException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

}
