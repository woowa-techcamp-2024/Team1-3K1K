package camp.woowak.lab.customer.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class DuplicateEmailException extends BadRequestException {
	public DuplicateEmailException() {
		super(CustomerErrorCode.DUPLICATE_EMAIL);
	}
}
