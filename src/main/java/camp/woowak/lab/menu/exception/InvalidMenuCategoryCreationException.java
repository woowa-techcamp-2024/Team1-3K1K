package camp.woowak.lab.menu.exception;

import camp.woowak.lab.common.exception.BadRequestException;
import camp.woowak.lab.common.exception.ErrorCode;

public class InvalidMenuCategoryCreationException extends BadRequestException {

	public InvalidMenuCategoryCreationException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

}
