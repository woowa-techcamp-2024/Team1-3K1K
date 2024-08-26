package camp.woowak.lab.order.service.command;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

public record RetrieveOrderListCommand(Long storeId, UUID vendorId, LocalDateTime createdAfter, LocalDateTime createdBefore, Pageable pageable) {
	public RetrieveOrderListCommand(UUID vendorId, Pageable pageable) {
		this(null, vendorId, null, null, pageable);
	}
}
