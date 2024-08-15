package camp.woowak.lab.customer.exception;

import camp.woowak.lab.common.exception.UnauthorizedException;

public class CustomerAuthenticationException extends UnauthorizedException {
	public CustomerAuthenticationException(String message) {
		super(CustomerErrorCode.AUTHENTICATION_FAILED, message);
	}
}
