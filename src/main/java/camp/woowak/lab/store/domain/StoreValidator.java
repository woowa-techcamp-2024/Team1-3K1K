package camp.woowak.lab.store.domain;

import java.time.LocalDateTime;

import camp.woowak.lab.store.exception.StoreException;

public class StoreValidator {

	private static final int UNIT_OF_MIN_ORDER_PRICE = 1000;

	private static final int MIN_ORDER_PRICE = 5000;

	private static final int MIN_NAME_LENGTH = 2;
	private static final int MAX_NAME_LENGTH = 10;

	public static void validate(final String name, final String address, final Integer minOrderPrice,
								final LocalDateTime startTime, final LocalDateTime endTime
	) {
		validateName(name);
	}

	private static void validateName(final String name) {
		if (MIN_NAME_LENGTH <= name.length() && name.length() <= MAX_NAME_LENGTH) {
			return;
		}
		throw new StoreException("가게 이름은 2글자 ~ 10글자 이어야합니다.");
	}

}
