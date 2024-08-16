package camp.woowak.lab.web.dao.store;

import static org.assertj.core.api.Assertions.*;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.dto.request.store.StoreInfoListRequest;
import camp.woowak.lab.web.dto.response.store.StoreInfoListResponse;

@SpringBootTest
@Transactional
@DisplayName("StoreDao 클래스")
class StoreDaoTest extends StoreDummiesFixture {
	private final StoreDao storeDao;
	private final int dummyCount;
	private List<Store> dummies;

	@Autowired
	public StoreDaoTest(StoreDao storeDao, StoreRepository storeRepository,
						StoreCategoryRepository storeCategoryRepository,
						VendorRepository vendorRepository, PayAccountRepository payAccountRepository) {
		super(storeRepository, storeCategoryRepository, vendorRepository, payAccountRepository);
		this.storeDao = storeDao;
		this.dummyCount = 105;
	}

	@BeforeEach
	void setUpDummies() {
		this.dummies = createDummyStores(dummyCount);
	}

	@Nested
	@DisplayName("findAllStoreList 메서드는")
	class FindAllStoreList {
		private final int size = StoreInfoListRequest.DEFAULT_PAGE_SIZE;

		@Test
		@DisplayName("Default 사이즈 만큼 요청한 페이지의 컨텐츠가 나온다.(페이지가 모두 찬 경우)")
		void testWithPageRequestOrderByIdAsc() {
			//given
			int page = 1;
			StoreInfoListRequest request = new StoreInfoListRequest(page);

			//when
			StoreInfoListResponse response = storeDao.findAllStoreList(request);

			//then
			assertThat(response).isNotNull();

			//기대되는 결과값을 만들기 위한 조건
			Comparator<Store> comparator = (o1, o2) -> Long.compare(o1.getId(), o2.getId());
			int offset = page * size;
			assertResponse(response, comparator, offset, size);
		}

		@Test
		@DisplayName("Default 사이즈 만큼 요청한 페이지의 컨텐츠가 나온다. (페이지가 모두 차지 않은 경우)")
		void testWithPageRequestOrderByIdAsc_Not_Enough_Stores() {
			//given
			int page = 5;
			StoreInfoListRequest request = new StoreInfoListRequest(page);

			//when
			StoreInfoListResponse response = storeDao.findAllStoreList(request);

			//then
			assertThat(response).isNotNull();

			//기대되는 결과값을 만들기 위한 조건
			Comparator<Store> comparator = (o1, o2) -> Long.compare(o1.getId(), o2.getId());
			int offset = page * size;
			assertResponse(response, comparator, offset, size);
		}
	}

	private void assertResponse(StoreInfoListResponse response, Comparator<Store> comparator, int offset, int size) {
		List<StoreInfoListResponse.InfoResponse> stores = response.getStores();

		List<Store> expectedList = dummies.stream()
			.sorted(comparator)
			.skip(offset)
			.limit(size)
			.toList();

		assertThat(stores.size()).isEqualTo(expectedList.size());
		for (int i = 0; i < stores.size(); i++) {
			StoreInfoListResponse.InfoResponse actual = stores.get(i);
			System.out.println("actual.getStoreId() = " + actual.getStoreId());
			Store expected = expectedList.get(i);
			assertStoresInfo(actual, expected);
		}
	}

	private void assertStoresInfo(StoreInfoListResponse.InfoResponse info1, Store store) {
		assertThat(info1.isOpen()).isEqualTo(store.isOpen());
		assertThat(info1.getName()).isEqualTo(store.getName());
		assertThat(info1.getCategory()).isEqualTo(store.getStoreCategory().getName());
		assertThat(info1.getMinOrderPrice()).isEqualTo(store.getMinOrderPrice());
	}
}