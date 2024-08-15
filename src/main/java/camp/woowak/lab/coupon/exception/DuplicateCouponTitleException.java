package camp.woowak.lab.coupon.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class DuplicateCouponTitleException extends BadRequestException {
	public DuplicateCouponTitleException(String message) {
		super(CouponErrorCode.DUPLICATE_COUPON_TITLE, message);
	}
}
