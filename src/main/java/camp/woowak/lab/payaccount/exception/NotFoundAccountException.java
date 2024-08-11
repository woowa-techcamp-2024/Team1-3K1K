package camp.woowak.lab.payaccount.exception;

public class NotFoundAccountException extends RuntimeException {
	public NotFoundAccountException(String message) {
		super(message);
	}
}
