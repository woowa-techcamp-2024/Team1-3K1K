package camp.woowak.lab.payaccount.exception;

public class InsufficientBalanceException extends RuntimeException {
	public InsufficientBalanceException(String message) {
		super(message);
	}
}
