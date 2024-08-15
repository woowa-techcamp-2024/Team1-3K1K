package camp.woowak.lab.common.exception;

public class BadRequestException extends HttpStatusException {
	@Deprecated
	public BadRequestException(ErrorCode errorCode) {
		super(errorCode);
	}

	public BadRequestException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
