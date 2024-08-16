package camp.woowak.lab.web.dao.store;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import camp.woowak.lab.store.domain.QStore;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.web.dto.request.store.StoreInfoListRequest;
import camp.woowak.lab.web.dto.response.store.StoreInfoListResponse;

@Repository
@Transactional(readOnly = true)
public class StoreDao {
	private final JPAQueryFactory qf;

	public StoreDao(JPAQueryFactory qf) {
		this.qf = qf;
	}

	/**
	 * TODO 페이지별 매장 리스트를 조회할 수 있다.
	 * TODO 주문이 많은 순으로 정렬할 수 있다.
	 * TODO 최소 주문 가격순으로 정렬할 수 있다.
	 * TODO 최소 주문 가격으로 필터링할 수 있다.
	 * TODO 매장의 카테고리로 필터링할 수 잇다.
	 */
	public StoreInfoListResponse findAllStoreList(StoreInfoListRequest request) {
		QStore store = QStore.store;

		List<Store> fetchResult = qf.select(store)
			.from(store)
			.offset(request.getPage()*request.getSize())
			.limit(request.getSize())
			.fetch();

		return StoreInfoListResponse.of(fetchResult);
	}
}
