package camp.woowak.lab.web.dao.order;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class OrderQuery {
	private String vendorId;
	private Long storeId;
	private LocalDateTime createdAfter;
	private LocalDateTime createdBefore;

	public OrderQuery() {
	}

	public OrderQuery(LocalDateTime createdAfter, LocalDateTime createdBefore, Long storeId, String vendorId) {
		this.createdAfter = createdAfter;
		this.createdBefore = createdBefore;
		this.storeId = storeId;
		this.vendorId = vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}
}
