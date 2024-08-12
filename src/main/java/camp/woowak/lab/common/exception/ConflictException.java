package camp.woowak.lab.common.exception;

public class ConflictException extends HttpStatusException {
	public ConflictException(ErrorCode errorCode) {
		super(errorCode);
	}
}
