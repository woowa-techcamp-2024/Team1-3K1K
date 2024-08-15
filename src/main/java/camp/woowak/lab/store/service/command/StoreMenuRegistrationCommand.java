package camp.woowak.lab.store.service.command;

import java.util.List;
import java.util.UUID;

public record StoreMenuRegistrationCommand(
	UUID vendorId,
	Long storeId,
	List<MenuLineItem> menuItems
) {
}
