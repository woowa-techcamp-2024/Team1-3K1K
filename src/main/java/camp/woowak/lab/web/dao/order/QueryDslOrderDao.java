package camp.woowak.lab.web.dao.order;

import static camp.woowak.lab.customer.domain.QCustomer.*;
import static camp.woowak.lab.order.domain.QOrder.*;
import static camp.woowak.lab.store.domain.QStore.*;
import static camp.woowak.lab.vendor.domain.QVendor.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import camp.woowak.lab.web.dto.response.order.OrderResponse;

@Repository
public class QueryDslOrderDao implements OrderDao {
	private final JPAQueryFactory queryFactory;

	public QueryDslOrderDao(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public Page<OrderResponse> findAll(OrderQuery query, Pageable pageable) {
		List<OrderResponse> content = queryFactory
			.select(Projections.constructor(OrderResponse.class,
				order.id,
				Projections.constructor(OrderResponse.RequesterInfo.class,
					customer.id,
					customer.name,
					customer.email,
					customer.phone
				),
				Projections.constructor(OrderResponse.StoreInfo.class,
					store.id,
					store.name,
					vendor.name,
					store.storeAddress.district,
					store.phoneNumber
				),
				Projections.list(
					Projections.constructor(OrderResponse.OrderItemInfo.class,
						order.orderItems.any().menuId,
						order.orderItems.any().price,
						order.orderItems.any().quantity,
						order.orderItems.any().totalPrice
					)
				)
			))
			.from(order)
			.join(order.requester, customer).fetchJoin()
			.join(order.store, store).fetchJoin()
			.join(store.owner, vendor).fetchJoin()
			.where(
				isStore(query.getStoreId()),
				isVendor(query.getVendorId()),
				createdAtAfter(query.getCreatedAfter()),
				createdAtBefore(query.getCreatedBefore())
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(order.count())
			.from(order)
			.join(order.store, store)  // 필요한 조인만 추가
			.where(
				isStore(query.getStoreId()),
				isVendor(query.getVendorId()),
				createdAtAfter(query.getCreatedAfter()),
				createdAtBefore(query.getCreatedBefore())
			);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	private BooleanExpression isStore(Long storeId) {
		return storeId != null ? order.store.id.eq(storeId) : null;
	}

	// Example: 필터 조건 메서드
	private BooleanExpression createdAtAfter(LocalDateTime createdAfter) {
		return createdAfter != null ? order.createdAt.after(createdAfter) : null;
	}

	private BooleanExpression createdAtBefore(LocalDateTime createdBefore) {
		return createdBefore != null ? order.createdAt.before(createdBefore) : null;
	}

	private BooleanExpression isVendor(UUID vendorId) {
		return vendorId != null ? vendor.id.eq(vendorId) : null;
	}
}
