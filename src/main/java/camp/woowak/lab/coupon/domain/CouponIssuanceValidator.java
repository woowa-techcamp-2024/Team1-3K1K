package camp.woowak.lab.coupon.domain;

import camp.woowak.lab.coupon.exception.ExpiredCouponException;
import camp.woowak.lab.coupon.exception.InvalidICreationIssuanceException;
import camp.woowak.lab.customer.domain.Customer;

public class CouponIssuanceValidator {
	private CouponIssuanceValidator() {
	}

	public static void validate(Customer customer, Coupon coupon) {
		validateNotNull(customer, coupon);
		validateNotExpired(coupon);

	}

	private static void validateNotExpired(Coupon coupon) {
		if (coupon.isExpired()) {
			throw new ExpiredCouponException("만료된 쿠폰입니다.");
		}
	}

	private static void validateNotNull(Object... objects) {
		for (var object : objects) {
			if (object == null) {
				throw new InvalidICreationIssuanceException("쿠폰 발급에 필요한 정보가 없습니다.");
			}
		}
	}
}
