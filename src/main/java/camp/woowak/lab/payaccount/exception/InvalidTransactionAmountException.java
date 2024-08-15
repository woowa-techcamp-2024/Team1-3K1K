package camp.woowak.lab.payaccount.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class InvalidTransactionAmountException extends BadRequestException {
	public InvalidTransactionAmountException(String message) {
		super(PayAccountErrorCode.INVALID_TRANSACTION_AMOUNT, message);
	}
}
