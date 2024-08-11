package camp.woowak.lab.web.dto.request;

import org.hibernate.validator.constraints.Length;

import camp.woowak.lab.web.validation.annotation.Phone;
import jakarta.validation.constraints.Email;

public record SignUpCustomerRequest(
	@Length(min = 1, max = 50)
	String name,
	@Email
	String email,
	@Length(min = 8, max = 20)
	String password,
	@Phone
	String phone
) {
}
