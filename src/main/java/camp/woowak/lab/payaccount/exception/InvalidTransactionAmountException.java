package camp.woowak.lab.payaccount.exception;

public class InvalidTransactionAmountException extends RuntimeException {
	public InvalidTransactionAmountException(String message) {
		super(message);
	}
}
