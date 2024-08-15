package camp.woowak.lab.coupon.domain;

import java.time.LocalDateTime;

import camp.woowak.lab.coupon.exception.InvalidCreationCouponException;

public class CouponValidator {
	private static final int MAX_TITLE_LENGTH = 100;
	private static final int MIN_DISCOUNT_AMOUNT = 0;
	private static final int MIN_QUANTITY = 0;

	private CouponValidator() {
	}

	public static void validate(String title, int discountAmount, int quantity, LocalDateTime expiredAt) {
		validateTitle(title);
		validateDiscountAmount(discountAmount);
		validateQuantity(quantity);
		validateExpiredDate(expiredAt);
	}

	private static void validateTitle(String title) {
		if (title == null || title.isEmpty()) {
			throw new InvalidCreationCouponException("title should not be null or empty");
		}

		if (title.length() > MAX_TITLE_LENGTH) {
			throw new InvalidCreationCouponException("title should not be longer than " + MAX_TITLE_LENGTH);
		}
	}

	private static void validateDiscountAmount(int discountAmount) {
		if (discountAmount < MIN_DISCOUNT_AMOUNT) {
			throw new InvalidCreationCouponException(
				discountAmount + "should be greater than or equal to " + MIN_DISCOUNT_AMOUNT);
		}
	}

	private static void validateQuantity(int quantity) {
		if (quantity < MIN_QUANTITY) {
			throw new InvalidCreationCouponException(quantity + "should be greater than " + MIN_QUANTITY);
		}
	}

	private static void validateExpiredDate(LocalDateTime expiredAt) {
		if (expiredAt.isBefore(LocalDateTime.now())) {
			throw new InvalidCreationCouponException("expiredAt should be after now");
		}
	}
}
