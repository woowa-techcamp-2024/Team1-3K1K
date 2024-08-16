package camp.woowak.lab.web.dao.store;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import camp.woowak.lab.store.domain.QStore;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.web.dto.response.store.StoreInfoResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional(readOnly = true)
public class StoreDao {
	private final JPAQueryFactory qf;
	public StoreDao(JPAQueryFactory qf) {
		this.qf = qf;
	}

	public StoreInfoResponse findAllStoreList() {
		QStore store = QStore.store;

		List<Store> fetchResult = qf.select(store)
			.from(store)
			.fetch();

		return StoreInfoResponse.of(fetchResult);
	}
}
