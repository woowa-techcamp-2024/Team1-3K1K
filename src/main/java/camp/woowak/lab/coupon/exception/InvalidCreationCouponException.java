package camp.woowak.lab.coupon.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class InvalidCreationCouponException extends BadRequestException {
	public InvalidCreationCouponException(String message) {
		super(CouponErrorCode.INVALID_CREATION, message);
	}
}
