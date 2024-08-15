package camp.woowak.lab.customer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.exception.CustomerAuthenticationException;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.customer.service.command.SignInCustomerCommand;
import camp.woowak.lab.fixture.CustomerFixture;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.domain.TestPayAccount;
import camp.woowak.lab.web.authentication.PasswordEncoder;

/**
 *
 */
@ExtendWith(MockitoExtension.class)
public class SignInCustomerServiceTest implements CustomerFixture {
	@InjectMocks
	private SignInCustomerService signInCustomerService;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("로그인 성공")
	void testSignIn() {
		// given
		PayAccount newPayAccount = new TestPayAccount(1L);
		Customer customer = createCustomer(newPayAccount, passwordEncoder);
		SignInCustomerCommand cmd = new SignInCustomerCommand(customer.getEmail(), customer.getPassword());
		given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
		given(passwordEncoder.matches(cmd.password(), customer.getPassword())).willReturn(true);

		// when & then
		assertDoesNotThrow(() -> signInCustomerService.signIn(cmd));
		verify(customerRepository).findByEmail(customer.getEmail());
		verify(passwordEncoder).matches(cmd.password(), customer.getPassword());
	}

	@Test
	@DisplayName("로그인 실패 - 이메일 없음")
	void testSignInFailEmailNotFound() {
		// given
		PayAccount newPayAccount = new TestPayAccount(1L);
		Customer customer = createCustomer(newPayAccount, passwordEncoder);
		SignInCustomerCommand cmd = new SignInCustomerCommand("InvalidCustomer@email.com", customer.getPassword());
		given(customerRepository.findByEmail(cmd.email())).willReturn(Optional.empty());

		// when & then
		assertThrows(CustomerAuthenticationException.class, () -> signInCustomerService.signIn(cmd));
		verify(customerRepository).findByEmail(cmd.email());
	}

	@Test
	@DisplayName("로그인 실패 - 패스워드 불일치")
	void testSignInFail() {
		// given
		PayAccount newPayAccount = new TestPayAccount(1L);
		Customer customer = createCustomer(newPayAccount, passwordEncoder);
		SignInCustomerCommand cmd = new SignInCustomerCommand(customer.getEmail(), customer.getPassword());
		given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
		given(passwordEncoder.matches(cmd.password(), customer.getPassword())).willReturn(false);

		// when & then
		assertThrows(CustomerAuthenticationException.class, () -> signInCustomerService.signIn(cmd));
		verify(customerRepository).findByEmail(customer.getEmail());
		verify(passwordEncoder).matches(cmd.password(), customer.getPassword());
	}
}
