package camp.woowak.lab.vendor.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class DuplicateEmailException extends BadRequestException {
	public DuplicateEmailException() {
		super(VendorErrorCode.DUPLICATE_EMAIL);
	}
}
