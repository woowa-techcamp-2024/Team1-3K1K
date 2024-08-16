package camp.woowak.lab.coupon.exception;

import camp.woowak.lab.common.exception.ConflictException;

public class ExpiredCouponException extends ConflictException {
	public ExpiredCouponException(String message) {
		super(CouponIssuanceErrorCode.EXPIRED_COUPON, message);
	}
}
