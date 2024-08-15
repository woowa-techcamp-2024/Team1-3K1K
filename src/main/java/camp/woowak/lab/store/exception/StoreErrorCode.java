package camp.woowak.lab.store.exception;

import org.springframework.http.HttpStatus;

import camp.woowak.lab.common.exception.ErrorCode;

public enum StoreErrorCode implements ErrorCode {
	NULL_EXIST(HttpStatus.BAD_REQUEST, "S0", "값이 존재해야 합니다."),

	INVALID_NAME_RANGE(HttpStatus.BAD_REQUEST, "S1", "가게 이름은 2글자 ~ 10글자 이어야합니다."),

	INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "S2", "가게 주소는 송파구만 가능합니다."),

	INVALID_MIN_ORDER_PRICE(HttpStatus.BAD_REQUEST, "S3", "최소 주문 금액은 5,000원 이상이어야 합니다."),
	INVALID_UNIT_OF_MIN_ORDER_PRICE(HttpStatus.BAD_REQUEST, "S4", "최소 주문 금액은 1,000원 단위이어야 합니다."),

	INVALID_TIME_UNIT(HttpStatus.BAD_REQUEST, "S5", "가게 시작 시간은 분 단위까지 가능합니다"),
	INVALID_TIME(HttpStatus.BAD_REQUEST, "S6", "가게 시작 시간은 종료 시간보다 이전이어야 합니다"),

	INVALID_STORE_CATEGORY(HttpStatus.BAD_REQUEST, "S7", "존재하지 않는 가게 카테고리입니다."),

	NOT_EQUALS_VENDOR(HttpStatus.BAD_REQUEST, "S8", "가게의 점주와 일치하지 않습니다."),
	NOT_FOUND_STORE(HttpStatus.BAD_REQUEST, "S9", "가게를 찾을 수 없습니다.");

	private final int status;
	private final String errorCode;
	private final String message;

	StoreErrorCode(HttpStatus status, String errorCode, String message) {
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
