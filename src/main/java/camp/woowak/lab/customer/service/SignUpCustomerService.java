package camp.woowak.lab.customer.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.exception.DuplicateEmailException;
import camp.woowak.lab.customer.exception.InvalidCreationException;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.customer.service.command.SignUpCustomerCommand;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@Service
public class SignUpCustomerService {
	private final CustomerRepository customerRepository;
	private final PayAccountRepository payAccountRepository;
	private final PasswordEncoder passwordEncoder;

	public SignUpCustomerService(CustomerRepository customerRepository, PayAccountRepository payAccountRepository,
								 PasswordEncoder passwordEncoder) {
		this.customerRepository = customerRepository;
		this.payAccountRepository = payAccountRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public Long signUp(SignUpCustomerCommand cmd) throws InvalidCreationException, DuplicateEmailException {
		PayAccount payAccount = new PayAccount();
		payAccountRepository.save(payAccount);

		Customer newCustomer = new Customer(cmd.name(), cmd.email(), cmd.password(), cmd.phone(), payAccount,
			passwordEncoder);

		try {
			customerRepository.save(newCustomer);
		} catch (DataIntegrityViolationException e) {
			throw new DuplicateEmailException();
		}
		return newCustomer.getId();
	}
}
