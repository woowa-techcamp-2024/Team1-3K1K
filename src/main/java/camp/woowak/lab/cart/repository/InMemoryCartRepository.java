package camp.woowak.lab.cart.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import camp.woowak.lab.cart.domain.Cart;

/**
 * TODO : Null체크에 대한 Validation
 */
@Repository
public class InMemoryCartRepository implements CartRepository {
	private static final Map<Long, Cart> cartMap = new ConcurrentHashMap<>();

	@Override
	public Optional<Cart> findByCustomerId(Long customerId) {
		return Optional.ofNullable(cartMap.get(customerId));
	}

	@Override
	public Cart save(Cart cart) {
		Long customerId = cart.getCustomerId();
		cartMap.put(customerId, cart);

		return cart;
	}

	@Override
	public void delete(Cart cart) {
		cartMap.remove(cart.getCustomerId());
	}
}
