package camp.woowak.lab.store.service;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public record StoreRegistrationRequest(

	@NotBlank
	String storeName,

	@NotBlank
	String storeAddress,

	@NotBlank
	String storePhoneNumber,

	@NotBlank
	String storeCategoryName,

	@NotBlank
	Integer storeMinOrderPrice,

	@NotBlank
	LocalDateTime storeStarTime,

	@NotBlank
	LocalDateTime storeEndTime

) {
}
