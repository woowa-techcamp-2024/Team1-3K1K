package camp.woowak.lab.fixture;

import java.time.LocalDate;

import camp.woowak.lab.store.TestStore;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.vendor.domain.Vendor;

public interface StoreFixture {
	default TestStore createTestStore(Long id, Vendor owner) {
		return new TestStore(id
			, owner, new StoreCategory("양식"), "3K1K 가게", "송파", "02-1234-5678", 5000,
			LocalDate.now().atTime(6, 0), LocalDate.now().atTime(23, 0));
	}
}
