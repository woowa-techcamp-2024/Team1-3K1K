package camp.woowak.lab.menu.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;

import camp.woowak.lab.fixture.MenuFixture;
import camp.woowak.lab.infra.date.DateTimeProvider;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreAddress;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@DataJpaTest
class MenuCategoryRepositoryTest implements MenuFixture {

	@Autowired
	VendorRepository vendorRepository;

	@Autowired
	StoreRepository storeRepository;

	@Autowired
	MenuCategoryRepository menuCategoryRepository;

	@Autowired
	StoreCategoryRepository storeCategoryRepository;

	@Autowired
	PayAccountRepository payAccountRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@TestConfiguration
	static class TestContextConfiguration {
		@Bean
		public PasswordEncoder passwordEncoder() {
			return new NoOpPasswordEncoder();
		}
	}

	@Test
	@DisplayName("[Success] 가게와 메뉴카테고리 이름이 있으면 조회를 성공한다")
	void success() {
		// given
		PayAccount payAccount = payAccountRepository.save(new PayAccount());

		Vendor vendor = createVendor(payAccount);
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
		PayAccount payAccount = payAccountRepository.save(new PayAccount());

		Vendor vendor = createVendor(payAccount);
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

	@Test
	@DisplayName("[Success] 가게와 이름이 겹치지 않으면 저장된다.")
	void successSave() {
		// given
		PayAccount testPayAccount = payAccountRepository.save(new PayAccount());
		Vendor testVendor = vendorRepository.save(
			new Vendor("testVendor", "test@test.com", "testPassword", "010-0000-0000", testPayAccount,
				passwordEncoder));
		StoreCategory testStoreCategory = storeCategoryRepository.save(new StoreCategory("중식"));
		Store testStore = storeRepository.save(createStore(testVendor, testStoreCategory));

		// when
		MenuCategory menuCategory = new MenuCategory(testStore, "something");
		MenuCategory savedMenuCategory = menuCategoryRepository.save(menuCategory);
		Long savedMenuCategoryId = savedMenuCategory.getId();
		menuCategoryRepository.flush();

		// then
		Optional<MenuCategory> findMenuCategory = menuCategoryRepository.findById(savedMenuCategoryId);
		Assertions.assertTrue(findMenuCategory.isPresent());
		Assertions.assertEquals(findMenuCategory.get().getId(), savedMenuCategoryId);
	}

	@Test
	@DisplayName("[Exception] 가게와 이름이 겹치면 예외가 발생한다.")
	void duplicateStoreAndName() {
		// given
		PayAccount testPayAccount = payAccountRepository.save(new PayAccount());
		Vendor testVendor = vendorRepository.save(
			new Vendor("testVendor", "test@test.com", "testPassword", "010-0000-0000", testPayAccount,
				passwordEncoder));
		StoreCategory testStoreCategory = storeCategoryRepository.save(new StoreCategory("중식"));
		Store testStore = storeRepository.save(createStore(testVendor, testStoreCategory));
		menuCategoryRepository.saveAndFlush(new MenuCategory(testStore, "something"));

		// when
		MenuCategory newMenuCategory = new MenuCategory(testStore, "something");

		// then
		Assertions.assertThrows(DataIntegrityViolationException.class,
			() -> menuCategoryRepository.save(newMenuCategory));
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

	private Vendor createVendor(PayAccount payAccount) {
		PasswordEncoder passwordEncoder = new NoOpPasswordEncoder();
		return new Vendor("vendorName", "vendorEmail@example.com", "vendorPassword", "010-0000-0000", payAccount,
			passwordEncoder);
	}

}
