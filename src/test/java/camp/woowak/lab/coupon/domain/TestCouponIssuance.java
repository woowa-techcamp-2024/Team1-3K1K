package camp.woowak.lab.coupon.domain;

import camp.woowak.lab.customer.domain.Customer;

public class TestCouponIssuance extends CouponIssuance {
	private final Long id;

	public TestCouponIssuance(Long id, Coupon coupon, Customer customer) {
		super(coupon, customer);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
