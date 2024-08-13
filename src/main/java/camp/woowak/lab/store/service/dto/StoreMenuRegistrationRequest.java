package camp.woowak.lab.store.service.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoreMenuRegistrationRequest(
	List<MenuLineItem> menuItems
) {

	public record MenuLineItem(
		@NotBlank
		String name,

		@NotBlank
		String imageUrl,

		@NotBlank
		String categoryName,

		@NotNull
		Integer price
	) {
	}

}
