package camp.woowak.lab.fixture;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.exception.InvalidCreationException;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.web.authentication.PasswordEncoder;

/**
 * CustomerFixture는 Customer와 관련된 테스트에서 공통적으로 사용되는 객체를 생성하는 인터페이스입니다.
 */
public interface CustomerFixture {
	default PayAccount createPayAccount() {
		return new PayAccount();
	}

	default Customer createCustomer(PayAccount payAccount, PasswordEncoder passwordEncoder) {
		try {
			return new Customer("customerName", "customerEmail@example.com", "customerPassword", "010-0000-0000",
				payAccount,
				passwordEncoder);
		} catch (InvalidCreationException e) {
			throw new RuntimeException(e);
		}
	}

}
