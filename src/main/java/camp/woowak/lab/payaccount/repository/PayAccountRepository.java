package camp.woowak.lab.payaccount.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import camp.woowak.lab.payaccount.domain.PayAccount;
import jakarta.persistence.LockModeType;

public interface PayAccountRepository extends JpaRepository<PayAccount, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT pa FROM PayAccount pa LEFT JOIN Customer c on c.payAccount = pa where c.id = :customerId")
	Optional<PayAccount> findByCustomerIdForUpdate(@Param("customerId") Long customerId);
}
