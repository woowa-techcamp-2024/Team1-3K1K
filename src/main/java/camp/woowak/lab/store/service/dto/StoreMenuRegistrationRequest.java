package camp.woowak.lab.store.service.dto;

import java.util.List;

import camp.woowak.lab.store.service.command.MenuLineItem;
import jakarta.validation.constraints.NotNull;

public record StoreMenuRegistrationRequest(
	@NotBlank(message = "가게 ID는 필수값입니다.")
	Long storeId,

	@NotNull(message = "등록할 메뉴는 필수값입니다.")
	List<MenuLineItem> menuItems
) {
}
