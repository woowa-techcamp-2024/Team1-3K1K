package camp.woowak.lab.payment.exception;

import camp.woowak.lab.common.exception.ConflictException;
import camp.woowak.lab.common.exception.ErrorCode;

public class CantSettleOrderPayment extends ConflictException {

	public CantSettleOrderPayment(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
	
}
