package camp.woowak.lab.cart.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.cart.persistence.jpa.entity.CartEntity;

public interface CartEntityRepository extends JpaRepository<CartEntity, Long> {
	Optional<CartEntity> findByCustomerId(UUID customerId);
}
