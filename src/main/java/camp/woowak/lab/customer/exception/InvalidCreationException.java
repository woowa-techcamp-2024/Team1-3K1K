package camp.woowak.lab.customer.exception;

import camp.woowak.lab.common.exception.BadRequestException;
import camp.woowak.lab.common.exception.ErrorCode;

public class InvalidCreationException extends BadRequestException {
	public InvalidCreationException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
