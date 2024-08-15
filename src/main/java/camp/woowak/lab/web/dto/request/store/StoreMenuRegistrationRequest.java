package camp.woowak.lab.web.dto.request.store;

import java.util.List;

import camp.woowak.lab.store.service.command.MenuLineItem;
import jakarta.validation.constraints.NotNull;

public record StoreMenuRegistrationRequest(
	@NotNull(message = "등록할 메뉴는 필수값입니다.")
	List<MenuLineItem> menuItems
) {
}
