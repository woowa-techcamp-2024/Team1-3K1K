package camp.woowak.lab.menu.domain;

import static camp.woowak.lab.menu.exception.MenuErrorCode.*;

import camp.woowak.lab.menu.exception.InvalidMenuCreationException;
import camp.woowak.lab.store.domain.Store;

public class MenuValidator {

	private static final int MAX_NAME_LENGTH = 10;

	public static void validate(final Store store, final MenuCategory menuCategory, final String name,
								final Long price, final Long stockCount, final String imageUrl) {
		validateNotNull(store, menuCategory, name, price, stockCount, imageUrl);
		validateNotBlank(name, imageUrl);
		validateNameLength(name);
		validateStockCount(stockCount);
		validatePriceNegative(price);
	}

	private static void validateNotNull(final Object... targets) {
		for (Object target : targets) {
			if (target == null) {
				throw new InvalidMenuCreationException(NULL_EXIST, target + "은 Null 이 될 수 없습니다.");
			}
		}
	}

	private static void validateNotBlank(final String... targets) {
		for (String target : targets) {
			if (target.isBlank()) {
				throw new InvalidMenuCreationException(BLANK_EXIST, target + "은 빈 문자열이거나 공백 문자열이 포함될 수 없습니다.");
			}
		}
	}

	private static void validateNameLength(final String name) {
		if (name.length() > MAX_NAME_LENGTH) {
			throw new InvalidMenuCreationException(INVALID_NAME_RANGE, "메뉴 이름은 " + MAX_NAME_LENGTH + "글자까지 가능합니다.");
		}
	}

	private static void validatePriceNegative(final Long price) {
		if (price <= 0) {
			throw new InvalidMenuCreationException(INVALID_PRICE, "메뉴의 가격은 양수만 가능합니다. cur: " + price);
		}
	}

	private static void validateStockCount(final Long stockCount) {
		if (stockCount <= 0) {
			throw new InvalidMenuCreationException(INVALID_STOCK_COUNT, "메뉴의 재고수는 1개 이상 가능합니다. cur: " + stockCount);
		}
	}

}
