package camp.woowak.lab.web.dao.cart;

import static camp.woowak.lab.menu.domain.QMenu.*;
import static camp.woowak.lab.store.domain.QStore.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.repository.CartRepository;
import camp.woowak.lab.web.dto.response.CartResponse;

@Repository
public class RedisCartDao implements CartDao {
	private final CartRepository cartRepository;
	private final JPAQueryFactory queryFactory;

	public RedisCartDao(@Qualifier("inMemoryCartRepository") CartRepository cartRepository,
						JPAQueryFactory queryFactory) {
		this.cartRepository = cartRepository;
		this.queryFactory = queryFactory;
	}

	@Override
	public CartResponse findByCustomerId(UUID customerId) {
		Optional<Cart> optionalCart = cartRepository.findByCustomerId(customerId.toString());
		if (optionalCart.isEmpty()) {
			return new CartResponse();
		}
		Cart cart = optionalCart.get();

		if (cart.getCartItems().isEmpty()) {
			return new CartResponse();
		}

		Long sId = cart.getCartItems().stream().findAny().get().getStoreId();
		Map<Long, Integer> menuIdsCount = new HashMap<>();
		Set<Long> menuIds = cart.getCartItems().stream()
			.map((cartItem) -> {
				int amount = cartItem.getAmount();
				menuIdsCount.put(cartItem.getMenuId(), amount);
				return cartItem.getMenuId();
			})
			.collect(Collectors.toSet());

		List<Tuple> results = queryFactory
			.select(
				store.id,
				store.name,
				store.minOrderPrice,
				menu.id,
				menu.name,
				menu.price,
				menu.stockCount
			)
			.from(store)
			.join(menu).on(menu.store.id.eq(store.id))
			.where(store.id.eq(sId).and(menu.id.in(menuIds)))
			.fetch();

		if (results.isEmpty()) {
			return null; // 또는 적절한 예외 처리
		}

		Tuple firstResult = results.get(0);
		Long storeId = firstResult.get(store.id);
		String storeName = firstResult.get(store.name);
		Integer minOrderPrice = firstResult.get(store.minOrderPrice);

		List<CartResponse.CartItemInfo> menuList = results.stream()
			.map(tuple -> new CartResponse.CartItemInfo(
				tuple.get(menu.id),
				tuple.get(menu.name),
				tuple.get(menu.price),
				menuIdsCount.get(tuple.get(menu.id)), // amount 대신 stockCount 사용
				tuple.get(menu.stockCount)
			))
			.collect(Collectors.toList());

		return new CartResponse(storeId, storeName, minOrderPrice, menuList);
	}
}
