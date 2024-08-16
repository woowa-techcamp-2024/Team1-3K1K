package camp.woowak.lab.order.service.command;

import java.util.UUID;

public record RetrieveOrderListCommand(Long storeId, UUID vendorId) {
	public RetrieveOrderListCommand(UUID vendorId) {
		this(null, vendorId);
	}
}
