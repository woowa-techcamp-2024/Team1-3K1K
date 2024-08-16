package camp.woowak.lab.customer.domain;

import java.util.UUID;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;

public class TestCustomer extends Customer {
	private final UUID id;

	public TestCustomer(UUID id, String customerName, String mail, String customerPassword, String s,
						PayAccount payAccount,
						NoOpPasswordEncoder noOpPasswordEncoder) {
		super(customerName, mail, customerPassword, s, payAccount, noOpPasswordEncoder);
		this.id = id;
	}

	@Override
	public UUID getId() {
		return id;
	}
}
