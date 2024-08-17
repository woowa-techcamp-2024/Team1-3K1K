package camp.woowak.lab.store.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.domain.TestPayAccount;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreAddress;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.store.service.response.StoreDisplayResponse;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class StoreDisplayServiceTest {

	@InjectMocks
	private StoreDisplayService storeDisplayService;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private MenuRepository menuRepository;

	@Nested
	@DisplayName("매장을 전시하는 기능은")
	class DisplayStoreTest {

		Vendor vendor;
		StoreCategory storeCategory;
		Store store;
		MenuCategory menuCategory;
		Menu menu1;
		Menu menu2;

		@Test
		@DisplayName("[Success] 매장 정보, 매장 카테고리 정보, 매장 메뉴 정보를 ResponseDTO 에 매핑하여 응답한다")
		void test() {
			// given
			long storeId = 1L;
			setup(storeId);
			given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
			given(menuRepository.findByStoreId(storeId)).willReturn(List.of(menu1, menu2));

			// when
			StoreDisplayResponse response = storeDisplayService.displayStore(storeId);

			// then
			assertStore(response, storeId);
			assertStoreCategory(response);
			assertMenu(response);
		}

		private void assertStore(StoreDisplayResponse response, long storeId) {
			assertThat(response.storeId()).isEqualTo(storeId);
			assertThat(response.storeName()).isEqualTo(store.getName());
			assertThat(response.storeAddress()).isEqualTo(store.getStoreAddress());
			assertThat(response.storePhoneNumber()).isEqualTo(store.getPhoneNumber());
			assertThat(response.storeMinOrderPrice()).isEqualTo(store.getMinOrderPrice());
			assertThat(response.storeStartTime()).isEqualTo(store.getStoreStartTime());
			assertThat(response.storeEndTime()).isEqualTo(store.getStoreEndTime());
		}

		private void assertStoreCategory(StoreDisplayResponse response) {
			assertThat(response.storeCategoryId()).isEqualTo(storeCategory.getId());
			assertThat(response.storeCategoryName()).isEqualTo(storeCategory.getName());
		}

		private void assertMenu(StoreDisplayResponse response) {
			assertThat(response.menus()).hasSize(2);
			assertThat(response.menus()).extracting("menuCategoryId").containsOnly(menuCategory.getId());
			assertThat(response.menus()).extracting("menuCategoryName").containsOnly(menuCategory.getName());
			assertThat(response.menus()).extracting("menuId").containsExactlyInAnyOrder(menu1.getId(), menu2.getId());
			assertThat(response.menus()).extracting("menuName").containsExactlyInAnyOrder("후라이드치킨", "양념치킨");
		}

		private void setup(long storeId) {
			vendor = createVendor();
			storeCategory = createStoreCategory();
			store = createValidStore(storeId, vendor, storeCategory);
			menuCategory = createMenuCategory(store);
			menu1 = createMenu(store, menuCategory, "후라이드치킨");
			menu2 = createMenu(store, menuCategory, "양념치킨");
		}

		private Store createValidStore(Long id, Vendor vendor, StoreCategory storeCategory) {
			LocalDateTime validStartDateFixture = LocalDateTime.of(2020, 1, 1, 1, 1);
			LocalDateTime validEndDateFixture = LocalDateTime.of(2020, 1, 1, 2, 1);
			String validNameFixture = "3K1K 가게";
			String validAddressFixture = StoreAddress.DEFAULT_DISTRICT;
			String validPhoneNumberFixture = "02-1234-5678";
			Integer validMinOrderPriceFixture = 5000;

			return new TestStore(id, vendor, storeCategory, validNameFixture, validAddressFixture,
				validPhoneNumberFixture,
				validMinOrderPriceFixture, validStartDateFixture, validEndDateFixture);
		}

		private Vendor createVendor() {
			String name = "vendor";
			String email = "validEmail@validEmail.com";
			String password = "validPassword";
			String phone = "010-0000-0000";
			PayAccount payAccount = new TestPayAccount(1L);
			PasswordEncoder passwordEncoder = new NoOpPasswordEncoder();

			return new Vendor(name, email, password, phone, payAccount, passwordEncoder);
		}

		private StoreCategory createStoreCategory() {
			return new StoreCategory("양식");
		}

		private MenuCategory createMenuCategory(Store store) {
			return new MenuCategory(store, "치킨카테고리");
		}

		private Menu createMenu(Store store, MenuCategory menuCategory, String name) {
			return new Menu(store, menuCategory, name, 10000, 50L, "image");
		}

	}

	static class TestStore extends Store {
		private final Long id;

		TestStore(Long id, Vendor vendor, StoreCategory storeCategory, String name, String address, String phoneNumber,
				  Integer minOrderPrice, LocalDateTime startTime, LocalDateTime endTime) {
			super(vendor, storeCategory, name, address, phoneNumber, minOrderPrice, startTime, endTime);
			this.id = id;
		}

		@Override
		public Long getId() {
			return this.id;
		}
	}

}