package camp.woowak.lab.fixture;

import camp.woowak.lab.coupon.domain.Coupon;
import camp.woowak.lab.coupon.domain.CouponIssuance;
import camp.woowak.lab.coupon.domain.TestCouponIssuance;
import camp.woowak.lab.customer.domain.Customer;

/**
 * CouponIssuanceFixture는 테스트에서 CouponIssuance를 생성할 때 사용하는 Fixture입니다.
 */
public interface CouponIssuanceFixture {
	default CouponIssuance createCouponIssuance(Long id, Coupon coupon, Customer customer) {
		return new TestCouponIssuance(id, coupon, customer);
	}
}
