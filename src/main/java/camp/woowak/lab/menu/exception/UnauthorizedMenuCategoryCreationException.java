package camp.woowak.lab.menu.exception;

import camp.woowak.lab.common.exception.UnauthorizedException;

public class UnauthorizedMenuCategoryCreationException extends UnauthorizedException {
	public UnauthorizedMenuCategoryCreationException(String message) {
		super(MenuErrorCode.UNAUTHORIZED_MENU_CATEGORY_CREATION, message);
	}
}
