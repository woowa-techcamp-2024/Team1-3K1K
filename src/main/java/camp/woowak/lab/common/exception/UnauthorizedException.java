package camp.woowak.lab.common.exception;

public class UnauthorizedException extends HttpStatusException {
	public UnauthorizedException(ErrorCode errorCode) {
		super(errorCode);
	}

	public UnauthorizedException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
