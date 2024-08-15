package camp.woowak.lab.common.exception;

public class NotFoundException extends HttpStatusException {
	@Deprecated
	public NotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}

	public NotFoundException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
