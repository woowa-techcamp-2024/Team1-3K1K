package camp.woowak.lab.payaccount.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class InsufficientBalanceException extends BadRequestException {
	public InsufficientBalanceException() {
		super(PayAccountErrorCode.INSUFFICIENT_BALANCE);
	}
}
