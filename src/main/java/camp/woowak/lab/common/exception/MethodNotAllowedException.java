package camp.woowak.lab.common.exception;

public class MethodNotAllowedException extends HttpStatusException {
	@Deprecated
	public MethodNotAllowedException(ErrorCode errorCode) {
		super(errorCode);
	}

	public MethodNotAllowedException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
