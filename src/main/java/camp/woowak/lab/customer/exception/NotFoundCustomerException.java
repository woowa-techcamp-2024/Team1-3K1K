package camp.woowak.lab.customer.exception;

import camp.woowak.lab.common.exception.NotFoundException;

public class NotFoundCustomerException extends NotFoundException {
	public NotFoundCustomerException(String message) {
		super(CustomerErrorCode.NOT_FOUND, message);
	}
}
