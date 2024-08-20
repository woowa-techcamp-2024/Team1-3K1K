package camp.woowak.lab.web.dao.order;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;

@Getter
public class OrderQuery {
	private UUID vendorId;
	private Long storeId;
	private LocalDateTime createdAfter;
	private LocalDateTime createdBefore;

	public OrderQuery() {
	}

	public OrderQuery(LocalDateTime createdAfter, LocalDateTime createdBefore, Long storeId, UUID vendorId) {
		this.createdAfter = createdAfter;
		this.createdBefore = createdBefore;
		this.storeId = storeId;
		this.vendorId = vendorId;
	}

	public void setVendorId(UUID vendorId) {
		this.vendorId = vendorId;
	}
}
