package camp.woowak.lab.menu.exception;

import org.springframework.http.HttpStatus;

import camp.woowak.lab.common.exception.ErrorCode;

public enum MenuErrorCode implements ErrorCode {

	NULL_EXIST(HttpStatus.BAD_REQUEST, "M0", "값이 존재해야 합니다."),
	BLANK_EXIST(HttpStatus.BAD_REQUEST, "M1", "빈 문자열이거나 공백 문자열이 포함되면 안됩니다."),
	INVALID_NAME_RANGE(HttpStatus.BAD_REQUEST, "M2", "이름의 길이 범위를 벗어났습니다."),

	INVALID_PRICE(HttpStatus.BAD_REQUEST, "M3", "메뉴의 가격 범위를 벗어났습니다."),

	NOT_FOUND_MENU_CATEGORY(HttpStatus.BAD_REQUEST, "M3", "메뉴 카테고리를 찾을 수 없습니다.");

	private final int status;
	private final String errorCode;
	private final String message;

	MenuErrorCode(HttpStatus status, String errorCode, String message) {
		this.status = status.value();
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
