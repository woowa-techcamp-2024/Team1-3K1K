package camp.woowak.lab.coupon.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CouponTest {
	@Test
	@DisplayName("쿠폰 생성 테스트 - 쿠폰은 고정 할인 금액, 개수, 만료일을 입력해 할인 쿠폰을 등록할 수 있다.")
	void testConstructWithFixedDiscount() {
		// given
		int discountAmount = 1000;
		int quantity = 100;
		LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);

		// when
		Coupon coupon = new Coupon(discountAmount, quantity, expiredAt);

		// then
		assertEquals(discountAmount, coupon.getDiscountAmount());
		assertEquals(quantity, coupon.getQuantity());
		assertEquals(expiredAt, coupon.getExpiredAt());
	}
}