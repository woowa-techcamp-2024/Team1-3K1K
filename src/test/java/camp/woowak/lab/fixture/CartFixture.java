package camp.woowak.lab.fixture;

import java.time.LocalDateTime;
import java.util.UUID;

import camp.woowak.lab.menu.TestMenu;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.store.TestStore;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.vendor.TestVendor;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.web.authentication.PasswordEncoder;

public interface CartFixture {
	default Vendor createSavedVendor(UUID id, PayAccount payAccount, PasswordEncoder passwordEncoder) {
		return new TestVendor(
			id, "vendorName", "vendorEmail@example.com", "vendorPassword", "010-0000-0000", payAccount,
			passwordEncoder);
	}

	default Store createSavedStore(Long id, Vendor vendor, String name, int minOrderPrice, LocalDateTime startTIme,
								   LocalDateTime endTime) {
		return new TestStore(id, vendor, name, "송파", "010-1234-5678", minOrderPrice, startTIme, endTime);
	}

	default Menu createSavedMenu(Long id, Store store, MenuCategory menuCategory, String name, int price) {
		return new TestMenu(id, store, menuCategory, name, price);
	}
}
