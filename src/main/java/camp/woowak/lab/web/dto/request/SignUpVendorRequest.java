package camp.woowak.lab.web.dto.request;

import org.hibernate.validator.constraints.Length;

import camp.woowak.lab.web.validation.annotation.Phone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpVendorRequest(
	@NotBlank @Length(min = 1, max = 50)
	String name,
	@NotBlank @Email
	String email,
	@NotBlank @Length(min = 8, max = 30)
	String password,
	@Phone
	String phone
) {
}
