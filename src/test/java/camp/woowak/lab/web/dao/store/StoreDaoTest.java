package camp.woowak.lab.web.dao.store;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.fixture.VendorFixture;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.dto.response.store.StoreInfoResponse;

@SpringBootTest
@Transactional
@DisplayName("StoreDao 클래스")
class StoreDaoTest implements VendorFixture {
	@Autowired
	private StoreRepository storeRepository;
	@Autowired
	private StoreCategoryRepository storeCategoryRepository;
	@Autowired
	private VendorRepository vendorRepository;
	@Autowired
	private PayAccountRepository payAccountRepository;
	@Autowired
	private StoreDao storeDao;

	private Store store1;
	private Store store2;

	@BeforeEach
	void setUp() {
		PayAccount payAccount = new PayAccount();
		payAccountRepository.save(payAccount);
		Vendor vendor = createVendor(payAccount, new NoOpPasswordEncoder());
		vendorRepository.saveAndFlush(vendor);
		StoreCategory storeCategory = new StoreCategory("CategoryName");
		storeCategoryRepository.saveAndFlush(storeCategory);

		store1 = new Store(vendor, storeCategory, "Store1", "송파", "123-456-7890", 6000,
			LocalDateTime.now().minusHours(1).withSecond(0).withNano(0),
			LocalDateTime.now().plusHours(1).withSecond(0).withNano(0));
		store2 = new Store(vendor, storeCategory, "Store2", "송파", "987-654-3210", 7000,
			LocalDateTime.now().minusHours(1).withSecond(0).withNano(0),
			LocalDateTime.now().minusMinutes(1).withSecond(0).withNano(0));
		storeRepository.saveAndFlush(store1);
		storeRepository.saveAndFlush(store2);
	}

	@Nested
	@DisplayName("findAllStoreList 메서드는")
	class FindAllStoreList {

		@Test
		@DisplayName("아무런 파라미터값이 없으면 id값을 기준으로 오름차순 정렬되어 보내진다.")
		void testWithNoneArgumentOrderByIdAsc() {
			StoreInfoResponse response = storeDao.findAllStoreList();

			assertThat(response).isNotNull();
			assertThat(response.getStores()).size().isEqualTo(2);

			StoreInfoResponse.InfoResponse info1 = response.getStores().get(0);
			assertStoresInfo(info1, store1);

			StoreInfoResponse.InfoResponse info2 = response.getStores().get(1);
			assertStoresInfo(info2, store2);
		}
	}

	private void assertStoresInfo(StoreInfoResponse.InfoResponse info1, Store store) {
		assertThat(info1.isOpen()).isEqualTo(store.isOpen());
		assertThat(info1.getName()).isEqualTo(store.getName());
		assertThat(info1.getCategory()).isEqualTo(store.getStoreCategory().getName());
		assertThat(info1.getMinOrderPrice()).isEqualTo(store.getMinOrderPrice());
	}
}