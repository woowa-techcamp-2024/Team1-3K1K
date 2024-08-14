package camp.woowak.lab.common.exception;

public class MethodNotAllowedException extends HttpStatusException {
	public MethodNotAllowedException(ErrorCode errorCode) {
		super(errorCode);
	}
}
