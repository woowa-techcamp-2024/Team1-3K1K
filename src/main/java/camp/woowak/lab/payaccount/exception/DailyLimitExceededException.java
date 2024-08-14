package camp.woowak.lab.payaccount.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class DailyLimitExceededException extends BadRequestException {
	public DailyLimitExceededException(String message) {
		super(PayAccountErrorCode.DAILY_LIMIT_EXCEED, message);
	}
}
