package camp.woowak.lab.coupon.domain;

import java.time.LocalDateTime;

import camp.woowak.lab.coupon.exception.InsufficientCouponQuantityException;

public class TestCoupon extends Coupon {
	private final Long id;
	private int quantity;
	private LocalDateTime expiredAt;

	public TestCoupon(Long id, String title, int discountAmount, int quantity, LocalDateTime expiredAt) {
		super(title, discountAmount, quantity, expiredAt);
		this.id = id;
		this.expiredAt = expiredAt;
		this.quantity = quantity;
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

	@Override
	public void decreaseQuantity() {
		if (!hasAvailableQuantity()) {
			throw new InsufficientCouponQuantityException("쿠폰의 수량이 부족합니다.");
		}
		this.quantity--;
	}

	@Override
	public boolean hasAvailableQuantity() {
		return this.quantity > 0;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
