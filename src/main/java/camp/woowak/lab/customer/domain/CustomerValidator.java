package camp.woowak.lab.customer.domain;

import camp.woowak.lab.customer.exception.CustomerErrorCode;
import camp.woowak.lab.customer.exception.InvalidCreationException;
import camp.woowak.lab.payaccount.domain.PayAccount;

public class CustomerValidator {

	public static void validateCreation(String name, String email, String password, String phone,
										PayAccount payAccount) throws InvalidCreationException {
		validateName(name);
		validateEmail(email);
		validatePassword(password);
		validatePhone(phone);
		validatePayAccount(payAccount);
	}

	public static void validateName(String name) throws InvalidCreationException {
		if (name == null || name.isBlank()) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_NAME_IS_NOT_BLANK);
		}
		if (name.length() > 50) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_NAME_IS_TOO_LONG);
		}
	}

	public static void validateEmail(String email) throws InvalidCreationException {
		if (email == null || email.isBlank()) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_EMAIL_IS_NOT_BLANK);
		}
		if (email.trim().length() > 100) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_EMAIL_IS_TOO_LONG);
		}
	}

	public static void validatePassword(String password) throws InvalidCreationException {
		if (password == null || password.isBlank()) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_PASSWORD_IS_NOT_BLANK);
		}
		if (password.trim().length() > 30) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_PASSWORD_IS_TOO_LONG);
		}
	}

	public static void validatePhone(String phone) throws InvalidCreationException {
		if (phone == null || phone.isBlank()) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_PHONE_IS_NOT_BLANK);
		}
		if (phone.trim().length() > 30) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_PHONE_IS_TOO_LONG);
		}
	}

	public static void validatePayAccount(PayAccount payAccount) throws InvalidCreationException {
		if (payAccount == null) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_PAY_ACCOUNT_IS_NOT_NULL);
		}
	}
}
