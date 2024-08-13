package camp.woowak.lab.menu.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.menu.exception.InvalidMenuCategoryCreationException;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreAddress;

class MenuCategoryTest {

	@Nested
	@DisplayName("메뉴 카테고리 생성은")
	class MenuCategoryCreateTest {

		Store storeFixture = createValidStore();

		@Nested
		@DisplayName("메뉴 카테고리에 대한 가게가")
		class StoreOfMenu {

			@Test
			@DisplayName("[Exception] Null 이면 InvalidMenuCategoryCreationException 이 발생한다")
			void isNull() {
				// given & when & then
				assertThatCode(() -> new MenuCategory(null, "가게"))
					.isInstanceOf(InvalidMenuCategoryCreationException.class);
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

}