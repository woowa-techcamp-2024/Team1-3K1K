package camp.woowak.lab.customer.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.customer.service.dto.CustomerDTO;

@Service
@Transactional(readOnly = true)
public class RetrieveCustomerService {
	private final CustomerRepository customerRepository;

	public RetrieveCustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	public List<CustomerDTO> retrieveAllCustomers() {
		return customerRepository.findAll().stream().map(CustomerDTO::new).toList();
	}
}
