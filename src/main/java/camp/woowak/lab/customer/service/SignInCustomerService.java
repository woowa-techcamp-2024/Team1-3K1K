package camp.woowak.lab.customer.service;

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

	public void signIn(SignInCustomerCommand cmd) {
		Customer byEmail = customerRepository.findByEmail(cmd.email());
		if (byEmail == null) {
			throw new CustomerAuthenticationException("invalid email");
		} else if (!passwordEncoder.matches(cmd.password(), byEmail.getPassword())) {
			throw new CustomerAuthenticationException("password not matched");
		}
	}
}
