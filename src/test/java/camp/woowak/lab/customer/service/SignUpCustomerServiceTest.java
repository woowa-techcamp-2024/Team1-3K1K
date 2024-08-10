package camp.woowak.lab.customer.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.exception.DuplicateEmailException;
import camp.woowak.lab.customer.exception.InvalidCreationException;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.customer.service.command.SignUpCustomerCommand;

@SpringBootTest
class SignUpCustomerServiceTest {

	@Autowired
	SignUpCustomerService signUpCustomerService;

	@Autowired
	CustomerRepository customerRepository;

	@BeforeEach
	void setUp() {
		customerRepository.deleteAll();
	}

	@Test
	@DisplayName("구매자 회원가입 테스트")
	void testSignUp() throws InvalidCreationException, DuplicateEmailException {
		SignUpCustomerCommand cmd = new SignUpCustomerCommand("name", "email", "password", "phone");

		Long id = signUpCustomerService.signUp(cmd);

		Customer customer = customerRepository.findById(id).orElseThrow();

		assertEquals("name", customer.getName());
		assertEquals("email", customer.getEmail());
		assertEquals("password", customer.getPassword());
		assertEquals("phone", customer.getPhone());
	}

	@Test
	@DisplayName("구매자 이메일 중복 회원가입 테스트")
	void testSignUpWithExistingEmail() {
		SignUpCustomerCommand cmd = new SignUpCustomerCommand("name", "email", "password", "phone");

		assertThrows(DuplicateEmailException.class, () -> {
			signUpCustomerService.signUp(cmd);
			signUpCustomerService.signUp(cmd);
		});
	}
}