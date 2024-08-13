package camp.woowak.lab.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.customer.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
	Customer findByEmail(String email);
}
