package camp.woowak.lab.customer.domain;

import camp.woowak.lab.customer.exception.CustomerErrorCode;
import camp.woowak.lab.customer.exception.InvalidCreationException;
import camp.woowak.lab.payaccount.domain.PayAccount;

public class CustomerValidator {
	private static final int MAX_NAME_LENGTH = 50;
	private static final int MAX_EMAIL_LENGTH = 100;
	private static final int MIN_PASSWORD_LENGTH = 8;
	private static final int MAX_PASSWORD_LENGTH = 30;
	private static final int MAX_PHONE_LENGTH = 30;

	private CustomerValidator() {
	}

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
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION, "Customer name cannot be blank");
		}
		if (name.length() > MAX_NAME_LENGTH) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION,
				"Customer name cannot be longer than 50 characters");
		}
	}

	public static void validateEmail(String email) throws InvalidCreationException {
		if (email == null || email.isBlank()) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION, "Customer email cannot be blank");
		}
		if (email.trim().length() > MAX_EMAIL_LENGTH) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION,
				"Customer email cannot be longer than 100 characters");
		}
	}

	public static void validatePassword(String password) throws InvalidCreationException {
		if (password == null || password.isBlank()) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION, "Customer password cannot be blank");
		}
		if (password.trim().length() < MIN_PASSWORD_LENGTH) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION,
				"Customer password cannot be shorter than 8 characters");
		}
		if (password.trim().length() > MAX_PASSWORD_LENGTH) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION,
				"Customer password cannot be longer than 30 characters");
		}
	}

	public static void validatePhone(String phone) throws InvalidCreationException {
		if (phone == null || phone.isBlank()) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION, "Customer phone cannot be blank");
		}
		if (phone.trim().length() > MAX_PHONE_LENGTH) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION,
				"Customer phone cannot be longer than 30 characters");
		}
	}

	public static void validatePayAccount(PayAccount payAccount) throws InvalidCreationException {
		if (payAccount == null) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION,
				"Customer payAccount cannot be null");
		}
	}
}
