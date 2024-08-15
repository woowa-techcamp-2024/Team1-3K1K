package camp.woowak.lab.common.exception;

abstract class HttpStatusException extends RuntimeException {
	private final ErrorCode errorCode;

	@Deprecated
	public HttpStatusException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public HttpStatusException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ErrorCode errorCode() {
		return errorCode;
	}
}
