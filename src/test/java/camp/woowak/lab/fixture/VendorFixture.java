package camp.woowak.lab.fixture;

import java.util.UUID;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.domain.TestPayAccount;
import camp.woowak.lab.vendor.TestVendor;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

public interface VendorFixture {
	default PayAccount createPayAccount() {
		return new PayAccount();
	}

	default Vendor createSavedVendor(UUID id, PayAccount payAccount, PasswordEncoder passwordEncoder) {
		return new TestVendor(
			id, "vendorName", "vendorEmail@example.com", "vendorPassword", "010-0000-0000", payAccount,
			passwordEncoder);
	}

	default Vendor createVendor(PayAccount payAccount, PasswordEncoder passwordEncoder) {
		return new Vendor("vendorName", "vendorEmail@example.com", "vendorPassword", "010-0000-0000", payAccount,
			passwordEncoder);
	}

	default TestVendor createTestVendor() {
		return new TestVendor(UUID.randomUUID(), "vendorName", "vendorEmail@example.com", "vendorPassword",
			"010-0000-0000", new TestPayAccount(1L),
			new NoOpPasswordEncoder());
	}
}
