package camp.woowak.lab.store.service.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuLineItem(
	@NotBlank(message = "메뉴 이름은 필수값입니다.")
	String name,

	@NotBlank(message = "사진은 필수값입니다.")
	String imageUrl,

	@NotBlank(message = "메뉴 카테고리 이름은 필수값입니다.")
	String categoryName,

	@NotNull(message = "메뉴 가격은 필수값입니다.")
	Integer price
) {
}
