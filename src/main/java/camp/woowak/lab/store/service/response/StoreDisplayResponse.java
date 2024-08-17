package camp.woowak.lab.store.service.response;

import java.time.LocalTime;
import java.util.UUID;

import camp.woowak.lab.store.domain.Store;

public record StoreDisplayResponse(

	Long storeId,
	String storeName,
	String storeAddress,
	String storePhoneNumber,
	Integer storeMinOrderPrice,

	Long storeCategoryId,
	String storeCategoryName,

	LocalTime storeStartTime,
	LocalTime storeEndTime,

	UUID vendorId,
	String vendorName

) {

	public static StoreDisplayResponse of(final Store store) {
		return new StoreDisplayResponse(
			store.getId(),
			store.getName(),
			store.getStoreAddress(),
			store.getPhoneNumber(),
			store.getMinOrderPrice(),

			store.getStoreCategoryId(),
			store.getStoreCategoryName(),

			store.getStoreStartTime(),
			store.getStoreEndTime(),

			store.getVendorId(),
			store.getVendorName()
		);
	}
}
