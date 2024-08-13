package camp.woowak.lab.common.exception;

public class ForbiddenException extends HttpStatusException {
	public ForbiddenException(ErrorCode errorCode) {
		super(errorCode);
	}
}
