package camp.woowak.lab.web.dto.request.vendor;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignInVendorRequest(
	@NotBlank @Email
	String email,
	@NotBlank @Length(min = 8, max = 30)
	String password
) {
}
