package camp.woowak.lab.coupon.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import camp.woowak.lab.coupon.domain.Coupon;
import camp.woowak.lab.coupon.repository.CouponRepository;
import camp.woowak.lab.fixture.CouponFixture;

@ExtendWith(MockitoExtension.class)
class IssueCouponServiceTest implements CouponFixture {
	@InjectMocks
	private IssueCouponService issueCouponService;

	@MockBean
	private CouponRepository couponRepository;

	@Test
	@DisplayName("쿠폰 생성 테스트 - 쿠폰은 고정 할인 금액, 개수, 만료일을 입력해 할인 쿠폰을 등록할 수 있다.")
	void testIssueCoupon() {
		// given
		int discountAmount = 1000;
		int quantity = 100;
		LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);
		Coupon coupon = createCoupon(discountAmount, quantity, expiredAt);
		IssueCouponCommand cmd = new IssueCouponCommand(discountAmount, quantity, expiredAt);
		given(couponRepository.save(cmd)).willReturn(coupon);
		// when
		Long id = issueCouponService.issueCoupon(cmd);

		// then
		assertEquals(discountAmount, coupon.getDiscountAmount());
		assertEquals(quantity, coupon.getQuantity());
		assertEquals(expiredAt, coupon.getExpiredAt());
	}

}