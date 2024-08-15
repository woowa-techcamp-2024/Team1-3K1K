package camp.woowak.lab.order.exception;

import org.springframework.http.HttpStatus;

import camp.woowak.lab.common.exception.ErrorCode;

public enum OrderErrorCode implements ErrorCode {
	EMPTY_CART(HttpStatus.BAD_REQUEST, "o_1_1", "장바구니가 비어있습니다."),
	MULTI_STORE_ORDER(HttpStatus.BAD_REQUEST, "o_1_2", "동시에 하나의 가게에 대한 메뉴만 주문할 수 있습니다."),
	NOT_ENOUGH_BALANCE(HttpStatus.BAD_REQUEST, "o_1_2", "잔고가 부족합니다.");

	private final int status;
	private final String errorCode;
	private final String message;

	OrderErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
