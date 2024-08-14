package camp.woowak.lab.store.domain;

import static camp.woowak.lab.store.exception.StoreException.ErrorCode.*;

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
		validateAddress(address);
		validateMinOrderPrice(minOrderPrice);
		validateUnitOrderPrice(minOrderPrice);
		validateTime(startTime, endTime);
	}

	private static void validateName(final String name) {
		if (MIN_NAME_LENGTH <= name.length() && name.length() <= MAX_NAME_LENGTH) {
			return;
		}
		throw new StoreException(INVALID_NAME_RANGE);
	}

	// TODO: 가게 위치 비즈니스 요구사항 구체화하면, 주소 검증 로직 수정 예정
	private static void validateAddress(final String address) {
		if (StoreAddress.DEFAULT_DISTRICT.equals(address)) {
			return;
		}
		throw new StoreException(INVALID_ADDRESS);
	}

	private static void validateMinOrderPrice(final Integer minOrderPrice) {
		if (minOrderPrice < MIN_ORDER_PRICE) {
			throw new StoreException(INVALID_MIN_ORDER_PRICE);
		}
	}

	private static void validateUnitOrderPrice(final Integer minOrderPrice) {
		if (minOrderPrice % UNIT_OF_MIN_ORDER_PRICE != 0) {
			throw new StoreException(INVALID_UNIT_OF_MIN_ORDER_PRICE);
		}
	}

	private static void validateTime(final LocalDateTime startTime,
									 final LocalDateTime endTime
	) {
		if (isInvalidStoreTimeUnit(startTime)) {
			throw new StoreException(INVALID_TIME_UNIT);
		}

		if (isInvalidStoreTimeUnit(endTime)) {
			throw new StoreException(INVALID_TIME_UNIT);
		}

		if (endTime.isBefore(startTime)) {
			throw new StoreException(INVALID_TIME);
		}

		if (startTime.isEqual(endTime)) {
			throw new StoreException(INVALID_TIME);
		}
	}

	private static boolean isInvalidStoreTimeUnit(final LocalDateTime target) {
		return target.getSecond() != 0 || target.getNano() != 0;
	}

}
