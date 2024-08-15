package camp.woowak.lab.store.service.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoreMenuRegistrationRequest(
	@NotBlank(message = "가게 ID는 필수값입니다.")
	Long storeId,

	@NotNull(message = "등록할 메뉴는 필수값입니다.")
	List<MenuLineItem> menuItems
) {

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

}
