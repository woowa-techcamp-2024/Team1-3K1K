package camp.woowak.lab.cart.persistence.redis.repository;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.repository.CrudRepository;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.persistence.redis.entity.RedisCartEntity;
import camp.woowak.lab.cart.repository.CartRepository;

@ConditionalOnProperty(name = "cart.repository", havingValue = "redis")
public interface RedisCartRepository extends CrudRepository<RedisCartEntity, String>, CartRepository {
	@Override
	default Optional<Cart> findByCustomerId(String customerId) {
		return findById(customerId).map(RedisCartEntity::toDomain);
	}

	@Override
	default Cart save(Cart cart) {
		RedisCartEntity entity = RedisCartEntity.fromDomain(cart);
		return save(entity).toDomain();
	}

	@Override
	default void delete(Cart cart) {
		deleteById(cart.getCustomerId());
	}
}
