package camp.woowak.lab.fixture;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.web.authentication.PasswordEncoder;

public interface VendorFixture {
	default PayAccount createPayAccount() {
		return new PayAccount();
	}

	default Vendor createVendor(PayAccount payAccount, PasswordEncoder passwordEncoder) {
		return new Vendor("vendorName", "vendorEmail@example.com", "vendorPassword", "010-0000-0000", payAccount,
			passwordEncoder);
	}
}
