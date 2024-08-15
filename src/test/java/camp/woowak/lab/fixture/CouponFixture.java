package camp.woowak.lab.fixture;

import java.time.LocalDateTime;

import camp.woowak.lab.coupon.domain.Coupon;

/**
 * CouponFixture는 테스트에서 사용할 Coupon 객체를 생성하는 역할을 합니다.
 */
public interface CouponFixture {
	default Coupon createCoupon(int discountAmount, int amount, LocalDateTime expiredAt) {
		return new Coupon(discountAmount, amount, expiredAt);
	}
}
