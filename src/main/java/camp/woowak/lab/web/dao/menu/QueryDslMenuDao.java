package camp.woowak.lab.web.dao.menu;

import static camp.woowak.lab.menu.domain.QMenuCategory.*;
import static camp.woowak.lab.store.domain.QStore.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import camp.woowak.lab.web.dto.response.store.MenuCategoryResponse;

@Repository
public class QueryDslMenuDao implements MenuDao {
	private final JPAQueryFactory queryFactory;

	public QueryDslMenuDao(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public Page<MenuCategoryResponse> findAllCategoriesByStoreId(Long storeId, Pageable pageable) {

		List<MenuCategoryResponse> content = queryFactory
			.select(Projections.constructor(MenuCategoryResponse.class,
				menuCategory.id,
				menuCategory.name))
			.from(menuCategory)
			.where(
				eqStoreId(storeId)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(menuCategory.count())
			.from(menuCategory)
			.where(
				eqStoreId(storeId)
			);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	private BooleanExpression eqStoreId(Long storeId) {
		return store.id.eq(storeId);
	}
}
