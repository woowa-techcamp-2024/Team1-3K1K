package camp.woowak.lab.cart.exception;

import org.springframework.http.HttpStatus;

import camp.woowak.lab.common.exception.ErrorCode;

public enum CartErrorCode implements ErrorCode {
	MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "ca_1_1", "해당 메뉴가 존재하지 않습니다."),
	OTHER_STORE_MENU(HttpStatus.BAD_REQUEST, "ca_1_2", "다른 매장의 메뉴는 등록할 수 없습니다."),
	STORE_NOT_OPEN(HttpStatus.BAD_REQUEST, "ca_1_3", "주문 가능한 시간이 아닙니다.");

	private final int status;
	private final String errorCode;
	private final String message;

	CartErrorCode(HttpStatus status, String errorCode, String message) {
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
