package camp.woowak.lab.store.service.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoreRegistrationRequest(

	@NotBlank(message = "가게 이름은 필수값입니다.")
	String storeName,

	@NotBlank(message = "가게 주소는 필수값입니다.")
	String storeAddress,

	@NotBlank(message = "가게 번호는 필수값입니다.")
	String storePhoneNumber,

	@NotBlank(message = "가게 카테고리 이름은 필수값입니다.")
	String storeCategoryName,

	@NotNull(message = "가게 최소 주문 가격은 필수값입니다.")
	Integer storeMinOrderPrice,

	@NotNull(message = "가게 시작 시간은 필수값입니다.")
	LocalDateTime storeStartTime,

	@NotNull(message = "가게 종료 시간은 필수값입니다.")
	LocalDateTime storeEndTime

) {
}
