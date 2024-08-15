package camp.woowak.lab.coupon.domain;

import java.time.LocalDateTime;

public class TestCoupon extends Coupon {
	private final Long id;

	public TestCoupon(Long id, String title, int discountAmount, int amount, LocalDateTime expiredAt) {
		super(title, discountAmount, amount, expiredAt);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
