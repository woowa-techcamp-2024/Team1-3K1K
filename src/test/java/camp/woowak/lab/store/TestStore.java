package camp.woowak.lab.store;

import java.time.LocalDateTime;

import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.vendor.domain.Vendor;

public class TestStore extends Store {
	private Long id;

	public TestStore(Long id, Vendor vendor, String name, String address, String phoneNumber, int minOrderPrice,
					 LocalDateTime startTime, LocalDateTime endTime) {
		super(vendor, new StoreCategory("중식"), name, address, phoneNumber, minOrderPrice, startTime, endTime);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
