package camp.woowak.lab.store.domain;

import static camp.woowak.lab.store.exception.StoreErrorCode.*;

import java.time.LocalDateTime;

import camp.woowak.lab.store.exception.InvalidStoreCreationException;
import camp.woowak.lab.vendor.domain.Vendor;

public class StoreValidator {

	private static final int UNIT_OF_MIN_ORDER_PRICE = 1000;

	private static final int MIN_ORDER_PRICE = 5000;

	private static final int MIN_NAME_LENGTH = 2;
	private static final int MAX_NAME_LENGTH = 10;

	public static void validate(final Vendor owner, StoreCategory storeCategory, final String name,
								final String address, final Integer minOrderPrice,
								final LocalDateTime startTime, final LocalDateTime endTime
	) {
		validateNotNull(owner, storeCategory, name, address, minOrderPrice, startTime, endTime);
		validateName(name);
		validateAddress(address);
		validateMinOrderPrice(minOrderPrice);
		validateUnitOrderPrice(minOrderPrice);
		validateTime(startTime, endTime);
	}

	private static void validateNotNull(Object... targets) {
		for (Object target : targets) {
			if (target == null) {
				throw new InvalidStoreCreationException(NULL_EXIST, "Null 값은 허용하지 않습니다.");
			}
		}
	}

	private static void validateName(final String name) {
		if (MIN_NAME_LENGTH <= name.length() && name.length() <= MAX_NAME_LENGTH) {
			return;
		}
		throw new InvalidStoreCreationException(INVALID_NAME_RANGE,
			"이름의 길이는 " + MIN_NAME_LENGTH + " ~ " + MAX_NAME_LENGTH + " 만 가능합니다. cur: " + name.length());
	}

	private static void validateAddress(final String address) {
		if (StoreAddress.DEFAULT_DISTRICT.equals(address)) {
			return;
		}
		throw new InvalidStoreCreationException(INVALID_ADDRESS, "주소는 송파만 가능합니다. cur: " + address);
	}

	private static void validateMinOrderPrice(final Integer minOrderPrice) {
		if (minOrderPrice < MIN_ORDER_PRICE) {
			throw new InvalidStoreCreationException(INVALID_MIN_ORDER_PRICE,
				"최소 주문 금액은 " + MIN_ORDER_PRICE + " 보다 이상이어야 합니다. cur: " + minOrderPrice);
		}
	}

	private static void validateUnitOrderPrice(final Integer minOrderPrice) {
		if (minOrderPrice % UNIT_OF_MIN_ORDER_PRICE != 0) {
			throw new InvalidStoreCreationException(INVALID_UNIT_OF_MIN_ORDER_PRICE,
				"최소 주문 금액의 단위는 천원이어야 합니다. cur: " + minOrderPrice);
		}
	}

	private static void validateTime(final LocalDateTime startTime,
									 final LocalDateTime endTime
	) {
		if (isInvalidStoreTimeUnit(startTime)) {
			throw new InvalidStoreCreationException(INVALID_TIME_UNIT, "가게 시간은 분단위까지 가능합니다. cur: " + startTime);
		}

		if (isInvalidStoreTimeUnit(endTime)) {
			throw new InvalidStoreCreationException(INVALID_TIME_UNIT, "가게 시간은 분단위까지 가능합니다. cur:" + endTime);
		}

		if (endTime.isBefore(startTime)) {
			throw new InvalidStoreCreationException(INVALID_TIME,
				"가게 종료 시간이 시작 시간보다 이전입니다. cur:" + startTime + "," + endTime);
		}

		if (startTime.isEqual(endTime)) {
			throw new InvalidStoreCreationException(INVALID_TIME, "가게 시작 시간과 종료 시간이 같습니다. cur:" + startTime + endTime);
		}
	}

	private static boolean isInvalidStoreTimeUnit(final LocalDateTime target) {
		return target.getSecond() != 0 || target.getNano() != 0;
	}

}
