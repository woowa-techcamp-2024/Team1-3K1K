package camp.woowak.lab.common.exception;

public class NotFoundException extends HttpStatusException {
	public NotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
