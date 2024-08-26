package camp.woowak.lab.web.dao.cart;

import static camp.woowak.lab.cart.persistence.jpa.entity.QCartEntity.*;
import static camp.woowak.lab.cart.persistence.jpa.entity.QCartItemEntity.*;
import static camp.woowak.lab.menu.domain.QMenu.*;
import static camp.woowak.lab.store.domain.QStore.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import camp.woowak.lab.web.dto.response.CartResponse;

@Repository
@ConditionalOnProperty(name = "cart.dao", havingValue = "jpa")
public class JpaCartDao implements CartDao {
	private final JPAQueryFactory queryFactory;

	public JpaCartDao(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public CartResponse findByCustomerId(UUID customerId) {
		List<Tuple> results = queryFactory
			.select(store.id, store.name, store.minOrderPrice,
				menu.id, menu.name, menu.price, cartItemEntity.amount, menu.stockCount)
			.from(cartEntity)
			.leftJoin(cartEntity.cartItems, cartItemEntity)
			.join(menu).on(menu.id.eq(cartItemEntity.menuId))
			.join(store).on(store.id.eq(cartItemEntity.storeId))
			.where(eqCustomerId(customerId))
			.fetch();

		if (results.isEmpty()) {
			return null; // 또는 적절한 처리
		}

		Long storeId = results.get(0).get(store.id);
		String storeName = results.get(0).get(store.name);
		Integer minOrderPrice = results.get(0).get(store.minOrderPrice);

		List<CartResponse.CartItemInfo> items = results.stream()
			.map(tuple -> new CartResponse.CartItemInfo(
				tuple.get(menu.id),
				tuple.get(menu.name),
				tuple.get(menu.price),
				tuple.get(cartItemEntity.amount),
				tuple.get(menu.stockCount)
			))
			.collect(Collectors.toList());

		return new CartResponse(storeId, storeName, minOrderPrice, items);
	}

	private BooleanExpression eqCustomerId(UUID customerId) {
		return cartEntity.customerId.eq(customerId);
	}
}
