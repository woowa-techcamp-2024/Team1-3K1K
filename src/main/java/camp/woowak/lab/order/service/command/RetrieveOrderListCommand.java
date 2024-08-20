package camp.woowak.lab.order.service.command;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

public record RetrieveOrderListCommand(Long storeId, UUID vendorId, Pageable pageable) {
	public RetrieveOrderListCommand(UUID vendorId, Pageable pageable) {
		this(null, vendorId, pageable);
	}
}
