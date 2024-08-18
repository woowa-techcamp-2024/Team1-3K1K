package camp.woowak.lab.web.dto.request.store;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MenuPriceUpdateRequest(
	@Min(value = 1, message = "price값은 음수 혹은 0이 될 수 없습니다.")
	@NotNull(message = "price값은 필수 입니다.")
	Integer price
) {
}
