package camp.woowak.lab.web.dto.request.customer;

import org.hibernate.validator.constraints.Length;

import camp.woowak.lab.web.validation.annotation.Phone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpCustomerRequest(
	@Length(min = 1, max = 50, message = "이름은 1자 이상 50자 이하여야 합니다.")
	String name,
	@NotBlank(message = "이메일은 필수 입력값입니다.")
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	String email,
	@Length(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
	String password,
	@Phone(message = "전화번호 형식이 올바르지 않습니다.")
	String phone
) {
}
