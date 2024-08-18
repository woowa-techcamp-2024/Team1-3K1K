package camp.woowak.lab.order.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class NotEnoughBalanceException extends BadRequestException {
	public NotEnoughBalanceException(String message) {
		super(OrderErrorCode.NOT_ENOUGH_BALANCE, message);
	}
}
