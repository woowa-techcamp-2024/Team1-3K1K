package camp.woowak.lab.store.service.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record StoreRegistrationCommand(

	UUID vendorId,

	String storeName,
	String storeAddress,
	String storePhoneNumber,
	String storeCategoryName,
	Integer storeMinOrderPrice,
	LocalDateTime storeStartTime,
	LocalDateTime storeEndTime
) {
}
