package camp.woowak.lab.menu.exception;

import camp.woowak.lab.common.exception.ForbiddenException;

public class MenuOwnerNotMatchException extends ForbiddenException {
	public MenuOwnerNotMatchException(String message) {
		super(MenuErrorCode.MENU_OWNER_NOT_MATCH, message);
	}
}
