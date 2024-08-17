package camp.woowak.lab.coupon.exception;

import camp.woowak.lab.common.exception.ConflictException;

public class DuplicateCouponTitleException extends ConflictException {
	public DuplicateCouponTitleException(String message) {
		super(CouponErrorCode.DUPLICATE_COUPON_TITLE, message);
	}
}
