package camp.woowak.lab.menu.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.menu.exception.InvalidMenuCreationException;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.domain.TestPayAccount;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreAddress;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

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

		@Nested
		@DisplayName("메뉴 이름이")
		class MenuName {

			@Test
			@DisplayName("[Success] 10글자 이하만 가능하다")
			void success() {
				// given & when & then
				assertThatCode(() -> new Menu(storeFixture, menuCategoryFixture, "1234567890", 1000, "image"))
					.doesNotThrowAnyException();
			}

			@Test
			@DisplayName("[Exception] Null 이면 InvalidMenuCreationException 이 발생한다")
			void isNull() {
				// given & when & then
				assertThatCode(() -> new Menu(storeFixture, menuCategoryFixture, null, 1000, "image"))
					.isInstanceOf(InvalidMenuCreationException.class);
			}

			@Test
			@DisplayName("[Exception] 빈 문자열이면 InvalidMenuCreationException 이 발생한다")
			void isEmpty() {
				// given & when & then
				assertThatCode(() -> new Menu(storeFixture, menuCategoryFixture, "", 1000, "image"))
					.isInstanceOf(InvalidMenuCreationException.class);
			}

			@Test
			@DisplayName("[Exception] 공백 문자열이면 InvalidMenuCreationException 이 발생한다")
			void isBlank() {
				// given & when & then
				assertThatCode(() -> new Menu(storeFixture, menuCategoryFixture, "   ", 1000, "image"))
					.isInstanceOf(InvalidMenuCreationException.class);
			}

			@Test
			@DisplayName("[Exception] 10 글자를 초과하면 InvalidMenuCreationException 이 발생한다")
			void greaterThanMaxNameLength() {
				// given & when & then
				assertThatCode(() -> new Menu(storeFixture, menuCategoryFixture, "12345678901", 1000, "image"))
					.isInstanceOf(InvalidMenuCreationException.class);
			}

		}

		@Nested
		@DisplayName("메뉴 가격이")
		class MenuPrice {

			@Test
			@DisplayName("[Success] 양수만 가능하다")
			void success() {
				// given & when & then
				assertThatCode(() -> new Menu(storeFixture, menuCategoryFixture, "1234567890", 1, "image"))
					.doesNotThrowAnyException();
			}

			@Test
			@DisplayName("[Exception] Null 이면 InvalidMenuCreationException 이 발생한다")
			void isNull() {
				// given & when & then
				assertThatCode(() -> new Menu(storeFixture, menuCategoryFixture, "메뉴이름", null, "image"))
					.isInstanceOf(InvalidMenuCreationException.class);
			}

			@Test
			@DisplayName("[Exception] 음수면 InvalidMenuCreationException 이 발생한다")
			void isNegative() {
				// given & when & then
				assertThatCode(() -> new Menu(storeFixture, menuCategoryFixture, "메뉴이름", -1, "image"))
					.isInstanceOf(InvalidMenuCreationException.class);
			}

			@Test
			@DisplayName("[Exception] 0이면 InvalidMenuCreationException 이 발생한다")
			void isZero() {
				// given & when & then
				assertThatCode(() -> new Menu(storeFixture, menuCategoryFixture, "메뉴이름", 0, "image"))
					.isInstanceOf(InvalidMenuCreationException.class);
			}

		}

		@Nested
		@DisplayName("메뉴 사진 url 이")
		class MenuDescription {

			@Test
			@DisplayName("[Exception] Null 이면 InvalidMenuCreationException 이 발생한다")
			void isNull() {
				// given & when & then
				assertThatCode(() -> new Menu(storeFixture, menuCategoryFixture, null, 1000, "image"))
					.isInstanceOf(InvalidMenuCreationException.class);
			}

			@Test
			@DisplayName("[Exception] 빈 문자열이면 InvalidMenuCreationException 이 발생한다")
			void isEmpty() {
				// given & when & then
				assertThatCode(() -> new Menu(storeFixture, menuCategoryFixture, "", 1000, "image"))
					.isInstanceOf(InvalidMenuCreationException.class);
			}

			@Test
			@DisplayName("[Exception] 공백 문자열이면 InvalidMenuCreationException 이 발생한다")
			void isBlank() {
				// given & when & then
				assertThatCode(() -> new Menu(storeFixture, menuCategoryFixture, "   ", 1000, "image"))
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

		return new Store(createVendor(), createStoreCategory(), validNameFixture, validAddressFixture,
			validPhoneNumberFixture,
			validMinOrderPriceFixture,
			validStartDateFixture, validEndDateFixture);
	}

	private MenuCategory createValidMenuCategory() {
		return new MenuCategory(storeFixture, "1234567890");
	}

	private Vendor createVendor() {
		PayAccount payAccount = new TestPayAccount(1L);
		PasswordEncoder passwordEncoder = new NoOpPasswordEncoder();

		return new Vendor("vendor",
			"validEmail@validEmail.com",
			"validPassword", "010-0000-0000", payAccount, passwordEncoder);
	}

	private StoreCategory createStoreCategory() {
		return new StoreCategory("양식");
	}

}