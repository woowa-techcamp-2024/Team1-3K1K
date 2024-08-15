package camp.woowak.lab.store.service.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuLineItem(
	@NotBlank(message = "음식 상품 이름은 필수값입니다.")
	String name,

	@NotNull(message = "음식 상품의 재고수는 필수값입니다.")
	Long stockCount,

	@NotBlank(message = "음식 상품의 사진은 필수값입니다.")
	String imageUrl,

	@NotBlank(message = "음식 상품 카테고리 이름은 필수값입니다.")
	String categoryName,

	@NotNull(message = "음식 상품 가격은 필수값입니다.")
	Integer price

) {
}
