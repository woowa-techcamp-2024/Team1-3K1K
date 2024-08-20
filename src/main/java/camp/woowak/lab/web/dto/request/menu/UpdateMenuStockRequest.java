package camp.woowak.lab.web.dto.request.menu;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateMenuStockRequest(
	@NotNull(message = "메뉴 ID는 필수입니다.")
	Long menuId,
	@Min(value = 0, message = "재고는 0 이상이어야 합니다.")
	int stock) {
}
