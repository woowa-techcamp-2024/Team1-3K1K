package camp.woowak.lab.order.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class MultiStoreOrderException extends BadRequestException {
	public MultiStoreOrderException(String message) {
		super(OrderErrorCode.MULTI_STORE_ORDER, message);
	}
}
