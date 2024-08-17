package camp.woowak.lab.store.service.response;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import camp.woowak.lab.menu.domain.Menu;
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
	String vendorName,

	List<MenuDisplayResponse> menus
) {

	public static StoreDisplayResponse of(final Store store, final List<MenuDisplayResponse> menus) {
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
			store.getVendorName(),
			menus
		);
	}

	public record MenuDisplayResponse(
		Long menuCategoryId,
		String menuCategoryName,

		Long menuId,
		String menuName,
		Integer menuPrice
	) {

		public static MenuDisplayResponse of(final Menu menu) {
			return new MenuDisplayResponse(
				menu.getMenuCategoryId(),
				menu.getMenuCategoryName(),

				menu.getId(),
				menu.getName(),
				menu.getPrice()
			);
		}
	}
}
