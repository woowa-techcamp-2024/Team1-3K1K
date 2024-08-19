package camp.woowak.lab.web.dto.response.store;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.fixture.VendorFixture;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;

class StoreInfoListResponseTest implements VendorFixture {
	private Store store1;
	private Store store2;

	@BeforeEach
	void setUp() {
		Vendor vendor = createSavedVendor(UUID.randomUUID(), new PayAccount(), new NoOpPasswordEncoder());
		StoreCategory storeCategory = new StoreCategory("CategoryName");

		store1 = new Store(vendor, storeCategory, "Store1", "송파", "123-456-7890", 6000,
			LocalDateTime.now().minusHours(1).withSecond(0).withNano(0),
			LocalDateTime.now().plusHours(1).withSecond(0).withNano(0));
		store2 = new Store(vendor, storeCategory, "Store2", "송파", "987-654-3210", 7000,
			LocalDateTime.now().minusHours(1).withSecond(0).withNano(0),
			LocalDateTime.now().minusMinutes(1).withSecond(0).withNano(0));
	}

	@Test
	void testStoreInfoResponseCreation() {
		StoreInfoListResponse response = StoreInfoListResponse.of(Arrays.asList(store1, store2));

		assertThat(response).isNotNull();
		assertThat(response.getStores()).size().isEqualTo(2);

		StoreInfoListResponse.InfoResponse info1 = response.getStores().get(0);
		assertStoresInfo(info1, store1);

		StoreInfoListResponse.InfoResponse info2 = response.getStores().get(1);
		assertStoresInfo(info2, store2);
	}

	private void assertStoresInfo(StoreInfoListResponse.InfoResponse info1, Store store) {
		assertThat(info1.isOpen()).isEqualTo(store.isOpen());
		assertThat(info1.getName()).isEqualTo(store.getName());
		assertThat(info1.getCategory()).isEqualTo(store.getStoreCategory().getName());
		assertThat(info1.getMinOrderPrice()).isEqualTo(store.getMinOrderPrice());
	}
}