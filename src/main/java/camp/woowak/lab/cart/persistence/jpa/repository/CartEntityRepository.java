package camp.woowak.lab.cart.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import camp.woowak.lab.cart.persistence.jpa.entity.CartEntity;
import jakarta.persistence.LockModeType;

public interface CartEntityRepository extends JpaRepository<CartEntity, Long> {
	@Lock(LockModeType.OPTIMISTIC)
	Optional<CartEntity> findByCustomerId(UUID customerId);
}
