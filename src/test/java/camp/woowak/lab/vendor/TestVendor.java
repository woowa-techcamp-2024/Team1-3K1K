package camp.woowak.lab.vendor;

import java.util.UUID;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.web.authentication.PasswordEncoder;

public class TestVendor extends Vendor {
	private UUID id;

	public TestVendor(UUID id, String name, String email, String password, String phone, PayAccount payAccount,
					  PasswordEncoder passwordEncoder) {
		super(name, email, password, phone, payAccount, passwordEncoder);
		this.id = id;
	}

	public UUID getId() {
		return id;
	}
}
