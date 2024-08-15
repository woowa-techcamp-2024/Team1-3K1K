package camp.woowak.lab.customer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.customer.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
	Optional<Customer> findByEmail(String email);
}
