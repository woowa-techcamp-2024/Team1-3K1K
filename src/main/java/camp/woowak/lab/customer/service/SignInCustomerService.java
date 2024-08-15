package camp.woowak.lab.customer.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.exception.CustomerAuthenticationException;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.customer.service.command.SignInCustomerCommand;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@Service
public class SignInCustomerService {
	private final CustomerRepository customerRepository;
	private final PasswordEncoder passwordEncoder;

	public SignInCustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
		this.customerRepository = customerRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * @throws CustomerAuthenticationException 이메일이 존재하지 않거나 비밀번호가 일치하지 않으면
	 */
	public UUID signIn(SignInCustomerCommand cmd) {
		Customer byEmail = customerRepository.findByEmail(cmd.email())
			.orElseThrow(() -> new CustomerAuthenticationException("email not found"));
		if (!byEmail.validatePassword(cmd.password(), passwordEncoder)) {
			throw new CustomerAuthenticationException("password not matched");
		}

		return byEmail.getId();
	}
}
