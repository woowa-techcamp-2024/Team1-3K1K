package camp.woowak.lab.vendor.exception;

import camp.woowak.lab.common.exception.NotFoundException;

public class NotFoundVendorException extends NotFoundException {
	public NotFoundVendorException() {
		super(VendorErrorCode.NOT_FOUND_VENDOR);
	}
}
