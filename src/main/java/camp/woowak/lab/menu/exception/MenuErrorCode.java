package camp.woowak.lab.menu.exception;

import org.springframework.http.HttpStatus;

import camp.woowak.lab.common.exception.ErrorCode;

public enum MenuErrorCode implements ErrorCode {
	DUPLICATE_MENU_CATEGORY(HttpStatus.BAD_REQUEST, "m_1", "이미 생성된 카테고리입니다."),
	UNAUTHORIZED_MENU_CATEGORY_CREATION(HttpStatus.FORBIDDEN, "m_2", "점주만 카테고리를 생성할 수 있습니다.");

	private final int status;
	private final String errorCode;
	private final String message;

	MenuErrorCode(HttpStatus httpStatus, String errorCode, String message) {
		this.status = httpStatus.value();
		this.errorCode = errorCode;
		this.message = message;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
