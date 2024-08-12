package camp.woowak.lab.common.exception;

public class HttpStatusException extends RuntimeException {
	private final ErrorCode errorCode;

	public HttpStatusException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public ErrorCode errorCode() {
		return errorCode;
	}
}
