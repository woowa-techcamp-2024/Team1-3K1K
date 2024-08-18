package camp.woowak.lab.menu.exception;

import org.springframework.http.HttpStatus;

import camp.woowak.lab.common.exception.ErrorCode;

public enum MenuErrorCode implements ErrorCode {

	DUPLICATE_MENU_CATEGORY(HttpStatus.BAD_REQUEST, "m_1", "이미 생성된 카테고리입니다."),
	UNAUTHORIZED_MENU_CATEGORY_CREATION(HttpStatus.FORBIDDEN, "m_2", "점주만 카테고리를 생성할 수 있습니다."),

	NULL_EXIST(HttpStatus.BAD_REQUEST, "m_3", "값이 존재해야 합니다."),
	BLANK_EXIST(HttpStatus.BAD_REQUEST, "m_4", "빈 문자열이거나 공백 문자열이 포함되면 안됩니다."),
	INVALID_NAME_RANGE(HttpStatus.BAD_REQUEST, "m_5", "이름의 길이 범위를 벗어났습니다."),

	INVALID_PRICE(HttpStatus.BAD_REQUEST, "m_6", "메뉴의 가격 범위를 벗어났습니다."),
	INVALID_STOCK_COUNT(HttpStatus.BAD_REQUEST, "m_7", "메뉴의 재고 개수는 1개 이상이어야 합니다."),

	NOT_FOUND_MENU_CATEGORY(HttpStatus.BAD_REQUEST, "M3", "메뉴 카테고리를 찾을 수 없습니다."),
	NOT_ENOUGH_STOCK(HttpStatus.BAD_REQUEST, "M4", "재고가 부족합니다."),
	INVALID_UPDATE_MENU_STOCK(HttpStatus.CONFLICT, "m_9", "메뉴의 재고를 변경할 수 없습니다."),

	NOT_EQUALS_OWNER(HttpStatus.BAD_REQUEST, "m_10", "매장의 점주와 일치하지 않습니다."),
	NOT_UPDATABLE_TIME(HttpStatus.CONFLICT, "m_11", "메뉴를 변경할 수 없는 시간입니다."),
	;

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
