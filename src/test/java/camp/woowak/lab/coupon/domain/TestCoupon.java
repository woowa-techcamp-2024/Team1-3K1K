package camp.woowak.lab.coupon.domain;

import java.time.LocalDateTime;

public class TestCoupon extends Coupon {
	private final Long id;
	private LocalDateTime expiredAt;

	public TestCoupon(Long id, String title, int discountAmount, int amount, LocalDateTime expiredAt) {
		super(title, discountAmount, amount, expiredAt);
		this.id = id;
		this.expiredAt = expiredAt;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setExpiredAt(LocalDateTime expiredAt) {
		this.expiredAt = expiredAt;
	}

	@Override
	public boolean isExpired() {
		return expiredAt.isBefore(LocalDateTime.now());
	}
}
