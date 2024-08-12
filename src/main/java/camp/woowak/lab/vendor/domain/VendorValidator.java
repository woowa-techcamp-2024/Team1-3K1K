package camp.woowak.lab.vendor.domain;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.vendor.exception.InvalidVendorCreationException;
import camp.woowak.lab.vendor.exception.VendorErrorCode;

public final class VendorValidator {
	private static final int MAX_NAME_LENGTH = 50;
	private static final int MAX_EMAIL_LENGTH = 100;
	private static final int MIN_PASSWORD_LENGTH = 8;
	private static final int MAX_PASSWORD_LENGTH = 30;
	private static final int MAX_PHONE_LENGTH = 30;

	public static void validate(final String name, final String email, final String password, final String phone,
		final PayAccount payAccount) throws InvalidVendorCreationException {
		checkName(name);
		checkEmail(email);
		checkPassword(password);
		checkPhone(phone);
		checkPayAccount(payAccount);
	}

	private static void checkName(String name) throws InvalidVendorCreationException {
		if (name == null || name.isBlank()) {
			throw new InvalidVendorCreationException(VendorErrorCode.INVALID_NAME_EMPTY);
		}
		if (name.length() > MAX_NAME_LENGTH) {
			throw new InvalidVendorCreationException(VendorErrorCode.INVALID_NAME_RANGE);
		}
	}

	private static void checkEmail(String email) throws InvalidVendorCreationException {
		if (email == null || email.isBlank()) {
			throw new InvalidVendorCreationException(VendorErrorCode.INVALID_EMAIL_EMPTY);
		}
		if (email.trim().length() > MAX_EMAIL_LENGTH) {
			throw new InvalidVendorCreationException(VendorErrorCode.INVALID_EMAIL_RANGE);
		}
	}

	private static void checkPassword(String password) throws InvalidVendorCreationException {
		if (password == null || password.isBlank()) {
			throw new InvalidVendorCreationException(VendorErrorCode.INVALID_PASSWORD_EMPTY);
		}
		if (password.trim().length() < MIN_PASSWORD_LENGTH || password.trim().length() > MAX_PASSWORD_LENGTH) {
			throw new InvalidVendorCreationException(VendorErrorCode.INVALID_PASSWORD_RANGE);
		}
	}

	private static void checkPhone(String phone) throws InvalidVendorCreationException {
		if (phone == null || phone.isBlank()) {
			throw new InvalidVendorCreationException(VendorErrorCode.INVALID_PHONE_EMPTY);
		}
		if (phone.trim().length() > MAX_PHONE_LENGTH) {
			throw new InvalidVendorCreationException(VendorErrorCode.INVALID_PHONE_RANGE);
		}
	}

	private static void checkPayAccount(PayAccount payAccount) throws InvalidVendorCreationException {
		if (payAccount == null) {
			throw new InvalidVendorCreationException(VendorErrorCode.INVALID_PAY_ACCOUNT_EMPTY);
		}
	}
}
