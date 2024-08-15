package camp.woowak.lab.menu.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class DuplicateMenuCategoryException extends BadRequestException {
	public DuplicateMenuCategoryException(String message) {
		super(MenuErrorCode.DUPLICATE_MENU_CATEGORY, message);
	}
}
