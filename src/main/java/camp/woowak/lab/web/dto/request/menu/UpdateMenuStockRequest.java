package camp.woowak.lab.web.dto.request.menu;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateMenuStockRequest(
	@NotNull
	Long menuId,
	@Min(value = 0)
	int stock) {
}
