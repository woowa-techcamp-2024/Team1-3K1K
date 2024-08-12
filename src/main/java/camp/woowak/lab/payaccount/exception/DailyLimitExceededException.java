package camp.woowak.lab.payaccount.exception;

public class DailyLimitExceededException extends RuntimeException {
	public DailyLimitExceededException(String message) {
		super(message);
	}
}
