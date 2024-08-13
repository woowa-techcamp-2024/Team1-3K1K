package camp.woowak.lab.menu.domain;

import camp.woowak.lab.menu.exception.InvalidMenuCategoryCreationException;
import camp.woowak.lab.store.domain.Store;

public class MenuCategoryValidator {

	private static final int MAX_NAME_LENGTH = 10;

	public static void validate(final Store store, final String name) {
		validateNotNull(store, name);
		validateNotBlank(name);
	}

	private static void validateNotNull(final Object... targets) {
		for (Object target : targets) {
			if (target == null) {
				throw new InvalidMenuCategoryCreationException(target + "은 Null 이 될 수 없습니다.");
			}
		}
	}

	private static void validateNotBlank(final String... targets) {
		for (String target : targets) {
			if (target.isBlank()) {
				throw new InvalidMenuCategoryCreationException(target + "은 빈 문자열이거나 공백 문자열이 포함될 수 없습니다.");
			}
		}
	}
	
}
