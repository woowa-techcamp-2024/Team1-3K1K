package camp.woowak.lab.web.dao;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.infra.date.DateTimeProvider;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.menu.repository.MenuCategoryRepository;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.dto.response.store.MenuCategoryResponse;

@SpringBootTest
@Transactional
class QueryDslMenuDaoTest {

	@Autowired
	private QueryDslMenuDao queryDslMenuDao;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private MenuCategoryRepository menuCategoryRepository;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private PayAccountRepository payAccountRepository;

	@Autowired
	private StoreCategoryRepository storeCategoryRepository;

	private Store testStore;

	@BeforeEach
	void setUp() {
		PayAccount testAccount = payAccountRepository.save(new PayAccount());
		Vendor testVendor = vendorRepository.save(
			new Vendor("Test Vendor", "TestEmail@email.com", "TestPassword", "010-0000-0000", testAccount,
				new NoOpPasswordEncoder()));
		StoreCategory testCategory = storeCategoryRepository.save(new StoreCategory("TestCategory"));
		// 테스트용 Store 생성
		DateTimeProvider fixedTimeProvider = () -> LocalDateTime.of(2024, 8, 24, 1, 0, 0);
		testStore = storeRepository.save(
			new Store(testVendor, testCategory, "Test Store", "송파", "010-1234-5678", 18000,
				fixedTimeProvider.now(), fixedTimeProvider.now().plusHours(10)));

		// 테스트용 MenuCategory 생성
		menuCategoryRepository.saveAll(List.of(
			new MenuCategory(testStore, "Category 1"),
			new MenuCategory(testStore, "Category 2"),
			new MenuCategory(testStore, "Category 3")
		));
	}

	@Nested
	@DisplayName("findAllCategoriesByStoreId는")
	class FindAllCategoriesByStoreIdIs {
		@Test
		@DisplayName("Page<MenuCategoryResponse>를 반환한다.")
		void findAllCategoriesByStoreId_ShouldReturnPagedMenuCategoryResponses() {
			// Given
			PageRequest pageable = PageRequest.of(0, 10);

			// When
			Page<MenuCategoryResponse> result = queryDslMenuDao.findAllCategoriesByStoreId(testStore.getId(), pageable);

			// Then
			assertThat(result.getContent()).hasSize(3);
			assertThat(result.getContent().get(0).getName()).isEqualTo("Category 1");
			assertThat(result.getContent().get(1).getName()).isEqualTo("Category 2");
			assertThat(result.getContent().get(2).getName()).isEqualTo("Category 3");
			assertThat(result.getTotalElements()).isEqualTo(3L);
		}
	}
}
