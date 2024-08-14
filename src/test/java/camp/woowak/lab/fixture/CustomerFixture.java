package camp.woowak.lab.fixture;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.exception.InvalidCreationException;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.web.authentication.PasswordEncoder;

public interface CustomerFixture {
	default PayAccount createPayAccount() {
		return new PayAccount();
	}

	default Customer createCustomer(PayAccount payAccount, PasswordEncoder passwordEncoder) {
		try {
			return new Customer("vendorName", "vendorEmail@example.com", "vendorPassword", "010-0000-0000", payAccount,
				passwordEncoder);
		} catch (InvalidCreationException e) {
			throw new RuntimeException(e);
		}
	}

}
