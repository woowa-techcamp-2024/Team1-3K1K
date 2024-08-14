package camp.woowak.lab.web.validation.validator;

import camp.woowak.lab.web.validation.annotation.Phone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<Phone, String> {
	private static final String PHONE_NUMBER_PATTERN = "^(01[0167]|02|0[3-6][1-4])-\\d{3,4}-\\d{4}$";

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}
		return value.matches(PHONE_NUMBER_PATTERN);
	}
}
