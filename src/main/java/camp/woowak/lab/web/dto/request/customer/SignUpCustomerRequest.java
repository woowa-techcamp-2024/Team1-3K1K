package camp.woowak.lab.web.dto.request.customer;

import org.hibernate.validator.constraints.Length;

import camp.woowak.lab.web.validation.annotation.Phone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpCustomerRequest(
	@Length(min = 1, max = 50)
	String name,
	@NotBlank
	@Email
	String email,
	@Length(min = 8, max = 20)
	String password,
	@Phone
	String phone
) {
}
