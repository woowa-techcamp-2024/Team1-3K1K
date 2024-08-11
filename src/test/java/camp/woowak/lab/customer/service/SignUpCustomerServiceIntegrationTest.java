package camp.woowak.lab.customer.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import camp.woowak.lab.customer.exception.DuplicateEmailException;
import camp.woowak.lab.customer.exception.InvalidCreationException;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.customer.service.command.SignUpCustomerCommand;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;

@SpringBootTest
class SignUpCustomerServiceIntegrationTest {

	@Autowired
	private SignUpCustomerService service;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PayAccountRepository payAccountRepository;

	@Test
	@DisplayName("이메일 중복 시 롤백 테스트")
	void testRollbackOnDuplicateEmail() throws InvalidCreationException, DuplicateEmailException {
		// given
		SignUpCustomerCommand command1 = new SignUpCustomerCommand("name1", "email@example.com", "password",
			"010-1234-5678");
		SignUpCustomerCommand command2 = new SignUpCustomerCommand("name2", "email@example.com", "password",
			"010-8765-4321");

		// when
		service.signUp(command1);

		assertEquals(1, customerRepository.count());
		assertEquals(1, payAccountRepository.count());

		// then
		try {
			service.signUp(command2);
			fail("중복 이메일 예외가 발생해야 합니다.");
		} catch (DuplicateEmailException e) {
			assertEquals(1, customerRepository.count());
			assertEquals(1, payAccountRepository.count());
		}
	}
}