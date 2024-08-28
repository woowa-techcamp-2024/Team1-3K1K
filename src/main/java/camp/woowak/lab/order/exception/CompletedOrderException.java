package camp.woowak.lab.order.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class CompletedOrderException extends BadRequestException {
	public CompletedOrderException(String message) {
		super(OrderErrorCode.ALREADY_COMPLETED_ORDER, message);
	}
}
