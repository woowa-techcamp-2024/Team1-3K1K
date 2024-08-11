package camp.woowak.lab.store.service.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoreRegistrationRequest(

	@NotBlank
	String storeName,

	@NotBlank
	String storeAddress,

	@NotBlank
	String storePhoneNumber,

	@NotBlank
	String storeCategoryName,

	@NotNull
	Integer storeMinOrderPrice,

	@NotNull
	LocalDateTime storeStarTime,

	@NotNull
	LocalDateTime storeEndTime

) {
}
