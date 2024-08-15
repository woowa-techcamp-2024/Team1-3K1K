package camp.woowak.lab.common.exception;

public class ForbiddenException extends HttpStatusException {
	@Deprecated
	public ForbiddenException(ErrorCode errorCode) {
		super(errorCode);
	}

	public ForbiddenException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
