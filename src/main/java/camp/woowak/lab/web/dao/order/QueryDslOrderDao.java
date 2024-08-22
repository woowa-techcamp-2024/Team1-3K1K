package camp.woowak.lab.web.dao.order;

import static camp.woowak.lab.customer.domain.QCustomer.*;
import static camp.woowak.lab.order.domain.QOrder.*;
import static camp.woowak.lab.order.domain.vo.QOrderItem.*;
import static camp.woowak.lab.store.domain.QStore.*;
import static camp.woowak.lab.vendor.domain.QVendor.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import camp.woowak.lab.order.domain.Order;

@Repository
public class QueryDslOrderDao implements OrderDao {
	private final JPAQueryFactory queryFactory;

	public QueryDslOrderDao(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public Page<Order> findAll(OrderQuery query, Pageable pageable) {
		List<Order> content = queryFactory
			.select(order)
			.from(order)
			.join(order.requester, customer).fetchJoin()
			.join(order.store, store).fetchJoin()
			.join(store.owner, vendor).fetchJoin()
			.join(order.orderItems, orderItem).fetchJoin()
			.where(
				isStore(query.getStoreId()),
				isOwner(query.getVendorId()),
				createdAtAfter(query.getCreatedAfter()),
				createdAtBefore(query.getCreatedBefore())
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(order.count())
			.from(order)
			.join(order.store, store)
			.join(store.owner, vendor)
			.where(
				isStore(query.getStoreId()),
				isOwner(query.getVendorId()),
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

	private BooleanExpression isOwner(UUID vendorId) {
		return vendorId != null ? order.store.owner.id.eq(vendorId) : null;
	}
}
