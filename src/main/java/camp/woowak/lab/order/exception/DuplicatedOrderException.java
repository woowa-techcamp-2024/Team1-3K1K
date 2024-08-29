package camp.woowak.lab.order.exception;

import camp.woowak.lab.common.exception.ConflictException;

public class DuplicatedOrderException extends ConflictException {
	public DuplicatedOrderException(String message) {
		super(OrderErrorCode.DUPLICATED_ORDER, message);
	}
}
