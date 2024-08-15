package camp.woowak.lab.fixture;

import java.time.LocalDateTime;
import java.util.UUID;

import camp.woowak.lab.menu.TestMenuCategory;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.domain.TestPayAccount;
import camp.woowak.lab.store.TestStore;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreAddress;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.vendor.TestVendor;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

public interface MenuFixture {
	default Vendor createVendor(UUID vendorId) {
		PayAccount payAccount = new TestPayAccount(1L);
		PasswordEncoder passwordEncoder = new NoOpPasswordEncoder();
		return new TestVendor(vendorId, "vendorName", "vendorEmail@example.com", "vendorPassword", "010-0000-0000",
			payAccount, passwordEncoder);
	}

	default Store createStore(Vendor owner) {
		LocalDateTime validStartDateFixture = LocalDateTime.of(2020, 1, 1, 1, 1);
		LocalDateTime validEndDateFixture = LocalDateTime.of(2020, 1, 1, 2, 1);
		String validNameFixture = "3K1K 가게";
		String validAddressFixture = StoreAddress.DEFAULT_DISTRICT;
		String validPhoneNumberFixture = "02-1234-5678";
		Integer validMinOrderPriceFixture = 5000;

		return new Store(owner, createStoreCategory(), validNameFixture, validAddressFixture,
			validPhoneNumberFixture,
			validMinOrderPriceFixture,
			validStartDateFixture, validEndDateFixture);
	}

	default Store createStore(Long id, Vendor owner) {
		LocalDateTime validStartDateFixture = LocalDateTime.of(2020, 1, 1, 1, 1);
		LocalDateTime validEndDateFixture = LocalDateTime.of(2020, 1, 1, 2, 1);
		String validNameFixture = "3K1K 가게";
		String validAddressFixture = StoreAddress.DEFAULT_DISTRICT;
		String validPhoneNumberFixture = "02-1234-5678";
		Integer validMinOrderPriceFixture = 5000;

		return new TestStore(id, owner, createStoreCategory(), validNameFixture, validAddressFixture,
			validPhoneNumberFixture, validMinOrderPriceFixture, validStartDateFixture, validEndDateFixture);
	}

	default MenuCategory createMenuCategory(Long id, Store store, String name) {
		return new TestMenuCategory(id, store, name);
	}

	default StoreCategory createStoreCategory() {
		return new StoreCategory("양식");
	}
}
