package camp.woowak.lab.vendor.exception;

import org.springframework.http.HttpStatus;

import camp.woowak.lab.common.exception.ErrorCode;

public enum VendorErrorCode implements ErrorCode {
	INVALID_PHONE_EMPTY(HttpStatus.BAD_REQUEST, "v_1_1", "전화번호가 입력되지 않았습니다."),
	INVALID_PHONE_RANGE(HttpStatus.BAD_REQUEST, "v_1_2", "전화번호는 30자를 넘을 수 없습니다."),
	INVALID_PASSWORD_EMPTY(HttpStatus.BAD_REQUEST, "v1_3", "비밀번호가 입력되지 않았습니다."),
	INVALID_PASSWORD_RANGE(HttpStatus.BAD_REQUEST, "v1_4", "비밀번호는 8-30자 입력되어야 합니다."),
	INVALID_EMAIL_EMPTY(HttpStatus.BAD_REQUEST, "v1_5", "이메일이 입력되지 않았습니다."),
	INVALID_EMAIL_RANGE(HttpStatus.BAD_REQUEST, "v1_6", "이메일은 100자를 넘을 수 없습니다."),
	INVALID_NAME_EMPTY(HttpStatus.BAD_REQUEST, "v1_7", "이름이 입력되지 않았습니다."),
	INVALID_NAME_RANGE(HttpStatus.BAD_REQUEST, "v1_8", "이름은 50자를 넘을 수 없습니다."),
	INVALID_PAY_ACCOUNT_EMPTY(HttpStatus.BAD_REQUEST, "v_1_9", "포인트 계좌가 입력되지 않았습니다."),
	DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "v_2", "이미 가입된 이메일입니다.");

	private final int status;
	private final String errorCode;
	private final String message;

	VendorErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
