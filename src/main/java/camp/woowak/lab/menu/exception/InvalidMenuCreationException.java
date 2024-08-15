package camp.woowak.lab.menu.exception;

import camp.woowak.lab.common.exception.BadRequestException;
import camp.woowak.lab.common.exception.ErrorCode;

public class InvalidMenuCreationException extends BadRequestException {

	public InvalidMenuCreationException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

}
