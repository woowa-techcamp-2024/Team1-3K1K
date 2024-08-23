package camp.woowak.lab.web.dao.cart;

import static camp.woowak.lab.cart.persistence.jpa.entity.QCartEntity.*;
import static camp.woowak.lab.cart.persistence.jpa.entity.QCartItemEntity.*;
import static camp.woowak.lab.menu.domain.QMenu.*;
import static camp.woowak.lab.store.domain.QStore.*;

import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
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
		CartResponse cartResponse = queryFactory
			.select(Projections.constructor(CartResponse.class,
				store.id,
				store.name,
				store.minOrderPrice,
				Projections.list(
					Projections.constructor(CartResponse.CartItemInfo.class,
						menu.id,
						menu.name,
						menu.price,
						cartItemEntity.amount,
						menu.stockCount
					)
				)
			))
			.from(cartEntity)
			.leftJoin(cartEntity.cartItems, cartItemEntity)
			.join(menu).on(menu.id.eq(cartItemEntity.menuId))
			.join(store).on(store.id.eq(cartItemEntity.storeId))
			.where(
				eqCustomerId(customerId)
			)
			.fetchOne();
		return cartResponse;
	}

	private BooleanExpression eqCustomerId(UUID customerId) {
		return cartEntity.customerId.eq(customerId);
	}
}
