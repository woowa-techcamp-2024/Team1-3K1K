package camp.woowak.lab.menu.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.menu.exception.InvalidMenuCreationException;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreAddress;

class MenuTest {

	Store storeFixture = createValidStore();
	MenuCategory menuCategoryFixture = createValidMenuCategory();

	@Nested
	@DisplayName("메뉴 생성은")
	class MenuCreateTest {

		@Nested
		@DisplayName("메뉴가 속한 가게가")
		class MenuOfStore {

			@Test
			@DisplayName("[Exception] Null 이면 InvalidMenuCreationException 이 발생한다")
			void isNull() {
				// given & when & then
				assertThatCode(() -> new Menu(null, menuCategoryFixture, "1234", 1000, "image"))
					.isInstanceOf(InvalidMenuCreationException.class);
			}

		}

		@Nested
		@DisplayName("메뉴카테고리가")
		class MenuCategory {

			@Test
			@DisplayName("[Exception] Null 이면 InvalidMenuCreationException 이 발생한다")
			void isNull() {
				// given & when & then
				assertThatCode(() -> new Menu(storeFixture, null, "1234", 1000, "image"))
					.isInstanceOf(InvalidMenuCreationException.class);
			}

		}

	}

	private Store createValidStore() {
		LocalDateTime validStartDateFixture = LocalDateTime.of(2020, 1, 1, 1, 1);
		LocalDateTime validEndDateFixture = LocalDateTime.of(2020, 1, 1, 2, 1);
		String validNameFixture = "3K1K 가게";
		String validAddressFixture = StoreAddress.DEFAULT_DISTRICT;
		String validPhoneNumberFixture = "02-1234-5678";
		Integer validMinOrderPriceFixture = 5000;

		return new Store(null, null, validNameFixture, validAddressFixture, validPhoneNumberFixture,
			validMinOrderPriceFixture,
			validStartDateFixture, validEndDateFixture);
	}

	private MenuCategory createValidMenuCategory() {
		return new MenuCategory(storeFixture, "1234567890");
	}

}