package camp.woowak.lab.cart.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.persistence.jpa.entity.CartEntity;
import camp.woowak.lab.cart.repository.CartRepository;

@Repository
@ConditionalOnProperty(name="cart.repository", havingValue = "jpa")
public class JpaCartRepository implements CartRepository {
	private final CartEntityRepository entityRepository;

	public JpaCartRepository(CartEntityRepository entityRepository) {
		this.entityRepository = entityRepository;
	}

	@Override
	public Optional<Cart> findByCustomerId(String customerId) {
		Optional<CartEntity> entity = entityRepository.findByCustomerId(UUID.fromString(customerId));
		if (entity.isEmpty()) {
			return Optional.empty();
		}
		return entity.map(CartEntity::toDomain);
	}

	@Override
	public Cart save(Cart cart) {
		CartEntity entity = CartEntity.fromDomain(cart);
		CartEntity save = entityRepository.save(entity);
		return save.toDomain();
	}

	@Override
	public void delete(Cart cart) {
		CartEntity cartEntity = CartEntity.fromDomain(cart);
		entityRepository.delete(cartEntity);
	}
}
