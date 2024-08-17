package camp.woowak.lab.web.dao.store;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import camp.woowak.lab.order.domain.QOrder;
import camp.woowak.lab.store.domain.QStore;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.web.dto.request.store.StoreFilterBy;
import camp.woowak.lab.web.dto.request.store.StoreInfoListRequest;
import camp.woowak.lab.web.dto.request.store.StoreInfoListRequestConst;
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

		List<Store> fetchResult = queryFactory
			.selectFrom(store)
			.leftJoin(order).on(order.store.eq(store))
			.groupBy(store.id)
			.where(
				eqCategoryName(request, store),
				goeMinPrice(request, store)
			)
			.orderBy(
				getOrderSpecifier(request, store, order)
			)
			.offset(request.getPage() * request.getSize())
			.limit(request.getSize())
			.fetch();

		return StoreInfoListResponse.of(fetchResult);
	}

	private BooleanExpression eqCategoryName(StoreInfoListRequest request, QStore store) {
		if (request.getFilterBy() == null || !request.getFilterBy().equals(StoreFilterBy.CATEGORY_NAME)) {
			return null;
		}
		String categoryName = request.getFilterValue();
		return categoryName != null && !categoryName.isBlank() ? store.storeCategory.name.eq(categoryName) : null;
	}

	private BooleanExpression goeMinPrice(StoreInfoListRequest request, QStore store) {
		if (request.getFilterBy() == null || !request.getFilterBy().equals(StoreFilterBy.MIN_PRICE)) {
			return null;
		}
		int minPrice = parseMinPrice(request.getFilterValue());
		return minPrice > 0 ? store.minOrderPrice.goe(minPrice) : null;
	}

	private OrderSpecifier<?>[] getOrderSpecifier(StoreInfoListRequest request, QStore store, QOrder order) {
		List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
		if (request.getSortBy() != null) {
			switch (request.getSortBy()) {
				case MIN_PRICE -> orderSpecifiers.add(request.getOrder() == StoreInfoListRequestConst.DEFAULT_ORDER ?
					store.minOrderPrice.asc() : store.minOrderPrice.desc());
				case ORDER_COUNT -> orderSpecifiers.add(request.getOrder() == StoreInfoListRequestConst.DEFAULT_ORDER ?
					order.count().asc() : order.count().desc());
			}
		}

		orderSpecifiers.add(store.id.asc());//중복된 값이 있으면 id를 기준으로 오름차순
		return orderSpecifiers.toArray(new OrderSpecifier<?>[0]);
	}

	private int parseMinPrice(String filterValue) {
		try {
			return Integer.parseInt(filterValue);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}