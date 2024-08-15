package camp.woowak.lab.cart.repository;

import java.util.Optional;

import camp.woowak.lab.cart.domain.Cart;

public interface CartRepository {
	Optional<Cart> findByCustomerId(Long customerId);

	Cart save(Cart cart);

	void delete(Cart cart);
}
