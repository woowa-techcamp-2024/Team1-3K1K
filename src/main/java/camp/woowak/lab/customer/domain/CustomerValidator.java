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
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION, "Customer name cannot be blank");
		}
		if (name.length() > 50) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION,
				"Customer name cannot be longer than 50 characters");
		}
	}

	public static void validateEmail(String email) throws InvalidCreationException {
		if (email == null || email.isBlank()) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION, "Customer email cannot be blank");
		}
		if (email.trim().length() > 100) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION,
				"Customer email cannot be longer than 100 characters");
		}
	}

	public static void validatePassword(String password) throws InvalidCreationException {
		if (password == null || password.isBlank()) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION, "Customer password cannot be blank");
		}
		if (password.trim().length() > 30) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION,
				"Customer password cannot be longer than 30 characters");
		}
	}

	public static void validatePhone(String phone) throws InvalidCreationException {
		if (phone == null || phone.isBlank()) {
			throw new InvalidCreationException(CustomerErrorCode.INVALID_CREATION, "Customer phone cannot be blank");
		}
		if (phone.trim().length() > 30) {
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
