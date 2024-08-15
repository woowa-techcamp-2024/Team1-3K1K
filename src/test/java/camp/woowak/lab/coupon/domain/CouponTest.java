package camp.woowak.lab.coupon.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.coupon.exception.InvalidCreationCouponException;

class CouponTest {
	@Test
	@DisplayName("쿠폰 생성 테스트 - 쿠폰은 제목, 고정 할인 금액, 개수, 만료일을 입력해 할인 쿠폰을 등록할 수 있다.")
	void testConstructWithFixedDiscount() {
		// given
		String title = "테스트 쿠폰";
		int discountAmount = 1000;
		int quantity = 100;
		LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);

		// when
		Coupon coupon = new Coupon(title, discountAmount, quantity, expiredAt);

		// then
		assertEquals(title, coupon.getTitle());
		assertEquals(discountAmount, coupon.getDiscountAmount());
		assertEquals(quantity, coupon.getQuantity());
		assertEquals(expiredAt, coupon.getExpiredAt());
	}

	@Test
	@DisplayName("쿠폰 생성 테스트 - 쿠폰의 개수는 0개 이상이어야 한다.")
	void testConstructWithQuantityZero() {
		// given
		String title = "테스트 쿠폰";
		int discountAmount = 1000;
		int quantity = -1;
		LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);

		// when & then
		assertThrows(InvalidCreationCouponException.class,
			() -> new Coupon(title, discountAmount, quantity, expiredAt));
	}

	@Test
	@DisplayName("쿠폰 생성 테스트 - 쿠폰의 할인 금액은 0원 이상이어야 한다.")
	void testConstructWithDiscountAmountZero() {
		// given
		String title = "테스트 쿠폰";
		int discountAmount = -1;
		int quantity = 100;
		LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);

		// when & then
		assertThrows(InvalidCreationCouponException.class,
			() -> new Coupon(title, discountAmount, quantity, expiredAt));
	}

	@Test
	@DisplayName("쿠폰 생성 테스트 - 쿠폰의 만료일은 현재 시간 이후여야 한다.")
	void testConstructWithExpiredAtBeforeNow() {
		// given
		String title = "테스트 쿠폰";
		int discountAmount = 1000;
		int quantity = 100;
		LocalDateTime expiredAt = LocalDateTime.now().minusDays(1);

		// when & then
		assertThrows(InvalidCreationCouponException.class,
			() -> new Coupon(title, discountAmount, quantity, expiredAt));
	}

	@Test
	@DisplayName("쿠폰 생성 테스트 - 쿠폰의 제목은 1자 이상이어야 한다.")
	void testConstructWithTitleEmpty() {
		// given
		String title = "";
		int discountAmount = 1000;
		int quantity = 100;
		LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);

		// when & then
		assertThrows(InvalidCreationCouponException.class,
			() -> new Coupon(title, discountAmount, quantity, expiredAt));
	}

	@Test
	@DisplayName("쿠폰 생성 테스트 - 쿠폰의 제목은 100자 이하여야 한다.")
	void testConstructWithTitleOver100() {
		// given
		String title = "a".repeat(101);
		int discountAmount = 1000;
		int quantity = 100;
		LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);

		// when & then
		assertThrows(InvalidCreationCouponException.class,
			() -> new Coupon(title, discountAmount, quantity, expiredAt));
	}
}