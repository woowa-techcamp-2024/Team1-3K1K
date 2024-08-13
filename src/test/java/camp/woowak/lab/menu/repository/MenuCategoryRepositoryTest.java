package camp.woowak.lab.menu.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import camp.woowak.lab.infra.date.DateTimeProvider;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreAddress;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;

@DataJpaTest
class MenuCategoryRepositoryTest {

	@Autowired
	VendorRepository vendorRepository;

	@Autowired
	StoreRepository storeRepository;

	@Autowired
	MenuCategoryRepository menuCategoryRepository;

	@Autowired
	StoreCategoryRepository storeCategoryRepository;

	@Nested
	@DisplayName("가게와 메뉴카테고리 이름으로 메뉴카테고리를 조회하는 기능은")
	class FindByStoreAndNameTest {

		@Test
		@DisplayName("[Success] 가게와 메뉴카테고리 이름이 있으면 조회를 성공한다")
		void success() {
			// given
			Vendor vendor = new Vendor();
			String categoryName = "돈가스";
			StoreCategory storeCategory = new StoreCategory(categoryName);

			vendorRepository.saveAndFlush(vendor);
			storeCategoryRepository.saveAndFlush(storeCategory);

			Store store = createStore(vendor, storeCategory);
			storeRepository.saveAndFlush(store);
			MenuCategory menuCategory = new MenuCategory(store, categoryName);
			menuCategoryRepository.save(menuCategory);

			// when & then
			assertThat(menuCategoryRepository.findByStoreIdAndName(store.getId(), categoryName))
				.isPresent()
				.containsSame(menuCategory);
		}

		@Test
		@DisplayName("[Exception] 가게가 없으면 빈 Optional 을 반환한다")
		void notExistStore() {
			// given
			String categoryName = "돈가스";

			// when & then
			assertThat(menuCategoryRepository.findByStoreIdAndName(1234567L, categoryName)).isEmpty();
		}

		@Test
		@DisplayName("[Exception] 메뉴카테고리 이름이 없으면 빈 Optional 을 반환한다")
		void notExistMenuCategoryName() {
			// given
			String categoryName = "돈가스";
			String notExistCategoryName = "xxx";

			Vendor vendor = new Vendor();
			vendorRepository.saveAndFlush(vendor);

			StoreCategory storeCategory = new StoreCategory(categoryName);
			storeCategoryRepository.saveAndFlush(storeCategory);

			Store store = createStore(vendor, storeCategory);
			storeRepository.saveAndFlush(store);

			MenuCategory menuCategory = new MenuCategory(store, categoryName);
			menuCategoryRepository.save(menuCategory);

			// when & then
			assertThat(menuCategoryRepository.findByStoreIdAndName(store.getId(), notExistCategoryName))
				.isEmpty();
		}

	}

	private Store createStore(Vendor vendor, StoreCategory storeCategory) {
		DateTimeProvider fixedStartTime = () -> LocalDateTime.of(2024, 8, 24, 1, 0, 0);
		DateTimeProvider fixedEndTime = () -> LocalDateTime.of(2024, 8, 24, 5, 0, 0);

		LocalDateTime validStartTimeFixture = fixedStartTime.now();
		LocalDateTime validEndTimeFixture = fixedEndTime.now();

		String validNameFixture = "3K1K 가게";
		String validAddressFixture = StoreAddress.DEFAULT_DISTRICT;
		String validPhoneNumberFixture = "02-0000-0000";
		Integer validMinOrderPriceFixture = 5000;

		return new Store(vendor,
			storeCategory,
			validNameFixture,
			validAddressFixture,
			validPhoneNumberFixture,
			validMinOrderPriceFixture,
			validStartTimeFixture,
			validEndTimeFixture
		);
	}

}