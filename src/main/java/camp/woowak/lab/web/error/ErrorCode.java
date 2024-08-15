package camp.woowak.lab.web.error;

public enum ErrorCode {
	AUTH_DUPLICATE_EMAIL("a1", "이미 가입된 이메일 입니다."),
	SIGNUP_INVALID_REQUEST("s1", "잘못된 요청입니다."),
	AUTH_INVALID_CREDENTIALS("a2", "이메일 또는 비밀번호가 올바르지 않습니다.");
	private final String code;
	private final String message;

	ErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
