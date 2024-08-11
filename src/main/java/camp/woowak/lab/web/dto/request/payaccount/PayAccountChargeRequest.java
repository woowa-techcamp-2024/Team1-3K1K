package camp.woowak.lab.web.dto.request.payaccount;

import jakarta.validation.constraints.NotNull;

public record PayAccountChargeRequest(
	@NotNull(message="amount can't be null")
	Long amount
) {
}
