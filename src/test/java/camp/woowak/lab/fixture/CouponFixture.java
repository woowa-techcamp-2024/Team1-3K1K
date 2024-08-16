package camp.woowak.lab.fixture;

import java.time.LocalDateTime;

import camp.woowak.lab.coupon.domain.Coupon;
import camp.woowak.lab.coupon.domain.TestCoupon;

/**
 * CouponFixture는 테스트에서 사용할 Coupon 객체를 생성하는 역할을 합니다.
 */
public interface CouponFixture {
	default Coupon createCoupon(String title, int discountAmount, int quantity, LocalDateTime expiredAt) {
		return new Coupon(title, discountAmount, quantity, expiredAt);
	}

	default Coupon createCoupon(Long id, String title, int discountAmount, int quantity, LocalDateTime expiredAt) {
		return new TestCoupon(id, title, discountAmount, quantity, expiredAt);
	}
}
