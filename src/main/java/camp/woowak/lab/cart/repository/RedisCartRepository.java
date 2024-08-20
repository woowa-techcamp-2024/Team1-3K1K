package camp.woowak.lab.cart.repository;

import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import camp.woowak.lab.cart.domain.Cart;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisCartRepository implements CartRepository {
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public Optional<Cart> findByCustomerId(String customerId) {
		return Optional.ofNullable((Cart)redisTemplate.opsForValue().get(customerId));
	}

	@Override
	public Cart save(Cart cart) {
		redisTemplate.opsForValue().set(cart.getCustomerId(), cart);
		return cart;
	}

	@Override
	public void delete(Cart cart) {
		redisTemplate.delete(cart.getCustomerId());
	}
}
