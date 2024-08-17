package camp.woowak.lab.order.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class MinimumOrderPriceNotMetException extends BadRequestException {
	public MinimumOrderPriceNotMetException(String message) {
		super(OrderErrorCode.MIN_ORDER_PRICE, message);
	}
}
