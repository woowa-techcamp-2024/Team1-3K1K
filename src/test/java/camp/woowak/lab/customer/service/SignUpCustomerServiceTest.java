package camp.woowak.lab.customer.service;

import static org.mockito.BDDMockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.exception.DuplicateEmailException;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.customer.service.command.SignUpCustomerCommand;
import camp.woowak.lab.fixture.CustomerFixture;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class SignUpCustomerServiceTest implements CustomerFixture {

	@InjectMocks
	private SignUpCustomerService service;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private PayAccountRepository payAccountRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("구매자 회원가입 테스트")
	void testSignUp() {
		// given
		given(passwordEncoder.encode(Mockito.anyString())).willReturn("password");
		PayAccount payAccount = createPayAccount();
		Customer customerMock = Mockito.mock(Customer.class);
		given(payAccountRepository.save(Mockito.any(PayAccount.class))).willReturn(payAccount);
		given(customerRepository.saveAndFlush(Mockito.any(Customer.class))).willReturn(customerMock);
		when(customerMock.getId()).thenReturn(UUID.randomUUID());

		// when
		SignUpCustomerCommand command =
			new SignUpCustomerCommand("name", "email@example.com", "password", "01012345678");
		service.signUp(command);

		// then
		then(payAccountRepository).should().save(Mockito.any(PayAccount.class));
		then(customerRepository).should().saveAndFlush(Mockito.any(Customer.class));
	}

	@Test
	@DisplayName("구매자 이메일 중복 회원가입 테스트")
	void testSignUpWithExistingEmail() {
		// given
		given(passwordEncoder.encode(Mockito.anyString())).willReturn("password");
		given(payAccountRepository.save(Mockito.any(PayAccount.class))).willReturn(createPayAccount());
		when(customerRepository.saveAndFlush(Mockito.any(Customer.class))).thenThrow(
			DataIntegrityViolationException.class);

		// when
		SignUpCustomerCommand command =
			new SignUpCustomerCommand("name", "email@example.com", "password", "01012345678");

		// then
		Assertions.assertThrows(DuplicateEmailException.class, () -> service.signUp(command));
		then(payAccountRepository).should().save(Mockito.any(PayAccount.class));
		then(customerRepository).should().saveAndFlush(Mockito.any(Customer.class));
	}
}
