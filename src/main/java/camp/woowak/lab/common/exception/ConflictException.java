package camp.woowak.lab.common.exception;

public class ConflictException extends HttpStatusException {
	@Deprecated
	public ConflictException(ErrorCode errorCode) {
		super(errorCode);
	}

	public ConflictException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
