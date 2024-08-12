package camp.woowak.lab.common.exception;

public class BadRequestException extends HttpStatusException {
	public BadRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
