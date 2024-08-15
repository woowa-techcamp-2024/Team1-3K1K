package camp.woowak.lab.menu.service.command;

import java.util.UUID;

public record MenuCategoryRegistrationCommand(
	UUID vendorId,
	Long storeId,
	String name
) {
}
