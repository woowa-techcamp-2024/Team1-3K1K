package camp.woowak.lab.order.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class NotFoundMenuException extends BadRequestException {
	public NotFoundMenuException(String message) {
		super(OrderErrorCode.NOT_FOUND_MENU, message);
	}
}
