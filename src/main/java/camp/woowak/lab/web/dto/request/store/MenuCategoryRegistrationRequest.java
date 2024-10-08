package camp.woowak.lab.web.dto.request.store;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record MenuCategoryRegistrationRequest(
	@NotBlank(message = "메뉴 카테고리 이름은 필수값입니다.") @Length(max = 10)
	String name
) {
}
