package camp.woowak.lab.customer.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.exception.NotFoundCustomerException;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
	Optional<Customer> findByEmail(String email);

	default Customer findByIdOrThrow(UUID id) {
		return findById(id).orElseThrow(() -> new NotFoundCustomerException("존재하지 않는 사용자를 조회했습니다."));
	}
}
