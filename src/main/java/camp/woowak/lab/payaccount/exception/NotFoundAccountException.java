package camp.woowak.lab.payaccount.exception;

import camp.woowak.lab.common.exception.NotFoundException;

public class NotFoundAccountException extends NotFoundException {
	public NotFoundAccountException() {
		super(PayAccountErrorCode.ACCOUNT_NOT_FOUND);
	}
}
