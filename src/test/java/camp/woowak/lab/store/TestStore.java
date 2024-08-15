package camp.woowak.lab.store;

import java.time.LocalDateTime;

import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.vendor.domain.Vendor;

public class TestStore extends Store {
	private Long id;

	public TestStore(Long id, Vendor owner, StoreCategory storeCategory, String name, String address,
					 String phoneNumber, Integer minOrderPrice, LocalDateTime startTime, LocalDateTime endTime
	) {
		super(owner, storeCategory, name, address, phoneNumber, minOrderPrice, startTime, endTime);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
