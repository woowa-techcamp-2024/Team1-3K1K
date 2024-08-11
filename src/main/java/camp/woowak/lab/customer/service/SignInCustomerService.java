package camp.woowak.lab.customer.service;

import org.springframework.stereotype.Service;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.exception.AuthenticationException;
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

	public void signIn(SignInCustomerCommand cmd) {
		Customer byEmail = customerRepository.findByEmail(cmd.email());
		if (byEmail == null || !passwordEncoder.matches(cmd.password(), byEmail.getPassword())) {
			throw new AuthenticationException("Invalid email or password");
		}
	}
}
