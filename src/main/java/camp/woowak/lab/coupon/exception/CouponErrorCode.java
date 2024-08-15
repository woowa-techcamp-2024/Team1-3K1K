package camp.woowak.lab.coupon.exception;

import org.springframework.http.HttpStatus;

import camp.woowak.lab.common.exception.ErrorCode;

public enum CouponErrorCode implements ErrorCode {
	INVALID_CREATION(HttpStatus.BAD_REQUEST, "cp_1_1", "잘못된 요청입니다."),
	;

	int status;
	String errorCode;
	String message;

	CouponErrorCode(HttpStatus status, String errorCode, String message) {
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
