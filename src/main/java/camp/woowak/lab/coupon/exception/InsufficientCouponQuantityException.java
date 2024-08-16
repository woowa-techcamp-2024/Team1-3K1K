package camp.woowak.lab.coupon.exception;

import camp.woowak.lab.common.exception.ConflictException;

public class InsufficientCouponQuantityException extends ConflictException {
	public InsufficientCouponQuantityException(String message) {
		super(CouponErrorCode.INSUFFICIENT_QUANTITY, message);
	}
}
