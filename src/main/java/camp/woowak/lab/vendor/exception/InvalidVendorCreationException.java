package camp.woowak.lab.vendor.exception;

import camp.woowak.lab.common.exception.BadRequestException;
import camp.woowak.lab.common.exception.ErrorCode;

public class InvalidVendorCreationException extends BadRequestException {
	public InvalidVendorCreationException(ErrorCode errorCode) {
		super(errorCode);
	}
}
