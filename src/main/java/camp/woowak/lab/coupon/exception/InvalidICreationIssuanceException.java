package camp.woowak.lab.coupon.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class InvalidICreationIssuanceException extends BadRequestException {
	public InvalidICreationIssuanceException(String message) {
		super(CouponIssuanceErrorCode.INVALID_ISSUANCE, message);
	}
}
