package camp.woowak.lab.coupon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import camp.woowak.lab.coupon.domain.Coupon;
import jakarta.persistence.LockModeType;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT c FROM Coupon c WHERE c.id = :id")
	Optional<Coupon> findByIdWithPessimisticLock(Long id);

	@Query("SELECT c FROM Coupon c WHERE c.id = :id")
	Optional<Coupon> findById(Long id);
}
