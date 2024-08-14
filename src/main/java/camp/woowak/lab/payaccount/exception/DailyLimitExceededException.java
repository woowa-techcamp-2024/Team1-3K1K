package camp.woowak.lab.payaccount.exception;

import camp.woowak.lab.common.exception.BadRequestException;

public class DailyLimitExceededException extends BadRequestException {
	public DailyLimitExceededException() {
		super(PayAccountErrorCode.DAILY_LIMIT_EXCEED);
	}
}
