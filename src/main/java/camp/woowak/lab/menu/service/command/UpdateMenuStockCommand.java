package camp.woowak.lab.menu.service.command;

import java.util.UUID;

public record UpdateMenuStockCommand(Long menuId, int stock, UUID vendorId) {
}
