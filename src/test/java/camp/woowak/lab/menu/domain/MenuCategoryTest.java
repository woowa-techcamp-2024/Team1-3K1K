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

		@Nested
		@DisplayName("이름이")
		class MenuName {

			@Test
			@DisplayName("[Success] 10글자 이하만 가능하다")
			void success() {
				// given & when & then
				assertThatCode(() -> new MenuCategory(storeFixture, "1234567890"))
					.doesNotThrowAnyException();
			}

			@Test
			@DisplayName("[Exception] Null 이면 InvalidMenuCreationException 이 발생한다")
			void isNull() {
				// given & when & then
				assertThatCode(() -> new MenuCategory(storeFixture, null))
					.isInstanceOf(InvalidMenuCategoryCreationException.class);
			}

			@Test
			@DisplayName("[Exception] 빈 문자열이면 InvalidMenuCreationException 이 발생한다")
			void isEmpty() {
				// given & when & then
				assertThatCode(() -> new MenuCategory(storeFixture, ""))
					.isInstanceOf(InvalidMenuCategoryCreationException.class);
			}

			@Test
			@DisplayName("[Exception] 공백 문자열이면 InvalidMenuCreationException 이 발생한다")
			void isBlank() {
				// given & when & then
				assertThatCode(() -> new MenuCategory(storeFixture, "    "))
					.isInstanceOf(InvalidMenuCategoryCreationException.class);
			}

			@Test
			@DisplayName("[Exception] 10 글자를 초과하면 InvalidMenuCreationException 이 발생한다")
			void greaterThanMaxNameLength() {
				// given & when & then
				assertThatCode(() -> new MenuCategory(storeFixture, "12345678901"))
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