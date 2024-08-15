package camp.woowak.lab.vendor.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class PasswordMismatchException extends BadRequestException {
	public PasswordMismatchException() {
		super(VendorErrorCode.WRONG_PASSWORD);
	}
}
