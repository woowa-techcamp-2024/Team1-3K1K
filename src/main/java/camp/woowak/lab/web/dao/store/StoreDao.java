package camp.woowak.lab.web.dao.store;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import camp.woowak.lab.order.domain.QOrder;
import camp.woowak.lab.store.domain.QStore;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.web.dto.request.store.StoreInfoListRequest;
import camp.woowak.lab.web.dto.request.store.StoreInfoListRequestConst;
import camp.woowak.lab.web.dto.request.store.StoreSortBy;
import camp.woowak.lab.web.dto.response.store.StoreInfoListResponse;

@Repository
@Transactional(readOnly = true)
public class StoreDao {
	private final JPAQueryFactory queryFactory;

	public StoreDao(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	public StoreInfoListResponse findAllStoreList(StoreInfoListRequest request) {
		QStore store = QStore.store;
		QOrder order = QOrder.order;

		JPAQuery<Store> query = buildBaseQuery(store, order, request);
		applyFilter(query, request, store);
		applyPagination(query, request);
		applyOrdering(query, request, store, order);

		List<Store> fetchResult = query.fetch();
		return StoreInfoListResponse.of(fetchResult);
	}

	private JPAQuery<Store> buildBaseQuery(QStore store, QOrder order, StoreInfoListRequest request) {
		JPAQuery<Store> query = queryFactory.selectFrom(store);
		if (StoreSortBy.ORDER_COUNT.equals(request.getSortBy())) {
			query.leftJoin(order).on(order.store.eq(store)).groupBy(store);
		}
		return query;
	}

	private void applyFilter(JPAQuery<Store> query, StoreInfoListRequest request, QStore store) {
		BooleanBuilder builder = new BooleanBuilder();
		if (request.getFilterBy() != null) {
			switch (request.getFilterBy()) {
				case CATEGORY_NAME -> applyCategoryNameFilter(builder, request, store);
				case MIN_PRICE -> applyMinPriceFilter(builder, request, store);
			}
		}
		query.where(builder);
	}

	private void applyCategoryNameFilter(BooleanBuilder builder, StoreInfoListRequest request, QStore store) {
		String categoryName = request.getFilterValue();
		if (categoryName != null && !categoryName.isBlank()) {
			builder.and(store.storeCategory.name.eq(categoryName));
		}
	}

	private void applyMinPriceFilter(BooleanBuilder builder, StoreInfoListRequest request, QStore store) {
		int minPrice = parseMinPrice(request.getFilterValue());
		if (minPrice > 0) {
			builder.and(store.minOrderPrice.goe(minPrice));
		}
	}

	private void applyPagination(JPAQuery<Store> query, StoreInfoListRequest request) {
		query.offset(request.getPage() * request.getSize()).limit(request.getSize());
	}

	private void applyOrdering(JPAQuery<Store> query, StoreInfoListRequest request, QStore store, QOrder order) {
		query.orderBy(getOrderSpecifiers(request, store, order));
	}

	private OrderSpecifier<?>[] getOrderSpecifiers(StoreInfoListRequest request, QStore store, QOrder order) {
		List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

		if (request.getSortBy() != null) {
			switch (request.getSortBy()) {
				case MIN_PRICE -> orderSpecifiers.add(getMinPriceOrderSpecifier(request, store));
				case ORDER_COUNT -> orderSpecifiers.add(getOrderCountOrderSpecifier(request, order));
			}
		}

		orderSpecifiers.add(store.id.asc());
		return orderSpecifiers.toArray(new OrderSpecifier[0]);
	}

	private OrderSpecifier<?> getMinPriceOrderSpecifier(StoreInfoListRequest request, QStore store) {
		return request.getOrder() == StoreInfoListRequestConst.DEFAULT_ORDER ? store.minOrderPrice.asc() :
			store.minOrderPrice.desc();
	}

	private OrderSpecifier<?> getOrderCountOrderSpecifier(StoreInfoListRequest request, QOrder order) {
		NumberExpression<Long> orderCount = order.count();
		return request.getOrder() == StoreInfoListRequestConst.DEFAULT_ORDER ? orderCount.asc() : orderCount.desc();
	}

	private int parseMinPrice(String filterValue) {
		try {
			return Integer.parseInt(filterValue);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}