package camp.woowak.lab.coupon.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.fixture.CouponFixture;
import camp.woowak.lab.fixture.CustomerFixture;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

class CouponIssuanceTest implements CouponFixture, CustomerFixture {
	private static PasswordEncoder passwordEncoder;

	@BeforeAll
	static void setUpAll() {
		passwordEncoder = new NoOpPasswordEncoder();
	}

	@Test
	@DisplayName("CouponIssuance 생성 테스트")
	void testConstruct() {
		// given
		Long fakeCouponId = 1L;
		String title = "할인 쿠폰";
		int discountAmount = 1000;
		int quantity = 100;
		LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);
		PayAccount payAccount = createPayAccount();
		Coupon coupon = createCoupon(fakeCouponId, title, discountAmount, quantity, expiredAt);
		Customer customer = createCustomer(payAccount, passwordEncoder);

		// when
		CouponIssuance couponIssuance = new CouponIssuance(coupon, customer);

		// then
		assertEquals(coupon, couponIssuance.getCoupon());
		assertEquals(customer, couponIssuance.getCustomer());
		assertNotNull(couponIssuance.getIssuedAt());
		assertNull(couponIssuance.getUsedAt());
	}
}
