package camp.woowak.lab.coupon.exception;

import org.springframework.http.HttpStatus;

import camp.woowak.lab.common.exception.ErrorCode;

public enum CouponIssuanceErrorCode implements ErrorCode {
	INVALID_ISSUANCE(HttpStatus.BAD_REQUEST, "cp_2_1", "잘못된 발급 요청입니다."),
	NOT_FOUND_COUPON(HttpStatus.NOT_FOUND, "cp_2_2", "존재하지 않는 쿠폰입니다."),
	EXPIRED_COUPON(HttpStatus.CONFLICT, "cp_2_3", "만료된 쿠폰입니다."),
	NOT_ENOUGH_COUPON(HttpStatus.CONFLICT, "cp_2_4", "쿠폰이 부족합니다."),
	;

	int status;
	String errorCode;
	String message;

	CouponIssuanceErrorCode(HttpStatus status, String errorCode, String message) {
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
