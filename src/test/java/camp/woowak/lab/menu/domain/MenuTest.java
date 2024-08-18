package camp.woowak.lab.menu.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.common.exception.ErrorCode;
import camp.woowak.lab.common.exception.HttpStatusException;
import camp.woowak.lab.menu.exception.InvalidMenuCreationException;
import camp.woowak.lab.menu.exception.InvalidMenuPriceUpdateException;
import camp.woowak.lab.menu.exception.MenuErrorCode;
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
				// given
				ErrorCode expected = MenuErrorCode.NULL_EXIST;

				// when
				Throwable thrown = catchThrowable(
					() -> new Menu(null, menuCategoryFixture, "1234", 1000, 50L, "image"));

				// then
				assertExceptionAndErrorCode(thrown, expected);
			}

		}

		@Nested
		@DisplayName("메뉴카테고리가")
		class MenuCategory {

			@Test
			@DisplayName("[Exception] Null 이면 InvalidMenuCreationException 이 발생한다")
			void isNull() {
				// given
				ErrorCode expected = MenuErrorCode.NULL_EXIST;

				// when
				Throwable thrown = catchThrowable(
					() -> new Menu(storeFixture, null, "1234", 1000, 50L, "image"));

				// then
				assertExceptionAndErrorCode(thrown, expected);
			}

		}

		@Nested
		@DisplayName("메뉴 이름이")
		class MenuName {

			@Test
			@DisplayName("[Success] 10글자 이하만 가능하다")
			void success() {
				// given & when & then
				assertThatCode(() -> new Menu(storeFixture, menuCategoryFixture, "1234567890", 1000, 50L, "image"))
					.doesNotThrowAnyException();
			}

			@Test
			@DisplayName("[Exception] Null 이면 InvalidMenuCreationException 이 발생한다")
			void isNull() {
				// given
				ErrorCode expected = MenuErrorCode.NULL_EXIST;

				// when
				Throwable thrown = catchThrowable(
					() -> new Menu(storeFixture, menuCategoryFixture, null, 1000, 50L, "image"));

				// then
				assertExceptionAndErrorCode(thrown, expected);
			}

			@Test
			@DisplayName("[Exception] 빈 문자열이면 InvalidMenuCreationException 이 발생한다")
			void isEmpty() {
				// given
				ErrorCode expected = MenuErrorCode.BLANK_EXIST;

				// when
				Throwable thrown = catchThrowable(
					() -> new Menu(storeFixture, menuCategoryFixture, "", 1000, 50L, "image"));

				// then
				assertExceptionAndErrorCode(thrown, expected);
			}

			@Test
			@DisplayName("[Exception] 공백 문자열이면 InvalidMenuCreationException 이 발생한다")
			void isBlank() {
				// given
				ErrorCode expected = MenuErrorCode.BLANK_EXIST;

				// when
				Throwable thrown = catchThrowable(
					() -> new Menu(storeFixture, menuCategoryFixture, "   ", 1000, 50L, "image"));

				// then
				assertExceptionAndErrorCode(thrown, expected);
			}

			@Test
			@DisplayName("[Exception] 10 글자를 초과하면 InvalidMenuCreationException 이 발생한다")
			void greaterThanMaxNameLength() {
				// given
				ErrorCode expected = MenuErrorCode.INVALID_NAME_RANGE;

				// when
				Throwable thrown = catchThrowable(
					() -> new Menu(storeFixture, menuCategoryFixture, "12345678901", 1000, 50L, "image"));

				// then
				assertExceptionAndErrorCode(thrown, expected);
			}

		}

		@Nested
		@DisplayName("메뉴 가격이")
		class MenuPrice {

			@Test
			@DisplayName("[Success] 양수만 가능하다")
			void success() {
				// given & when & then
				assertThatCode(() -> new Menu(storeFixture, menuCategoryFixture, "1234567890", 1, 50L, "image"))
					.doesNotThrowAnyException();
			}

			@Test
			@DisplayName("[Exception] Null 이면 InvalidMenuCreationException 이 발생한다")
			void isNull() {
				// given
				ErrorCode expected = MenuErrorCode.NULL_EXIST;

				// when
				Throwable thrown = catchThrowable(
					() -> new Menu(storeFixture, menuCategoryFixture, "메뉴이름", null, 50L, "image"));

				// then
				assertExceptionAndErrorCode(thrown, expected);
			}

			@Test
			@DisplayName("[Exception] 음수면 InvalidMenuCreationException 이 발생한다")
			void isNegative() {
				// given
				ErrorCode expected = MenuErrorCode.INVALID_PRICE;

				// when
				Throwable thrown = catchThrowable(
					() -> new Menu(storeFixture, menuCategoryFixture, "메뉴이름", -1, 50L, "image"));

				// then
				assertExceptionAndErrorCode(thrown, expected);
			}

			@Test
			@DisplayName("[Exception] 0이면 InvalidMenuCreationException 이 발생한다")
			void isZero() {
				// given
				ErrorCode expected = MenuErrorCode.INVALID_PRICE;

				// when
				Throwable thrown = catchThrowable(
					() -> new Menu(storeFixture, menuCategoryFixture, "메뉴이름", 0, 50L, "image"));

				// then
				assertExceptionAndErrorCode(thrown, expected);
			}

		}

		@Nested
		@DisplayName("메뉴 사진 url 이")
		class MenuDescription {

			@Test
			@DisplayName("[Exception] Null 이면 InvalidMenuCreationException 이 발생한다")
			void isNull() {
				// given
				ErrorCode expected = MenuErrorCode.NULL_EXIST;

				// when
				Throwable thrown = catchThrowable(
					() -> new Menu(storeFixture, menuCategoryFixture, null, 1000, 50L, "image"));

				// then
				assertExceptionAndErrorCode(thrown, expected);
			}

			@Test
			@DisplayName("[Exception] 빈 문자열이면 InvalidMenuCreationException 이 발생한다")
			void isEmpty() {
				// given
				ErrorCode expected = MenuErrorCode.BLANK_EXIST;

				// when
				Throwable thrown = catchThrowable(
					() -> new Menu(storeFixture, menuCategoryFixture, "", 1000, 50L, "image"));

				// then
				assertExceptionAndErrorCode(thrown, expected);
			}

			@Test
			@DisplayName("[Exception] 공백 문자열이면 InvalidMenuCreationException 이 발생한다")
			void isBlank() {
				// given
				ErrorCode expected = MenuErrorCode.BLANK_EXIST;

				// when
				Throwable thrown = catchThrowable(
					() -> new Menu(storeFixture, menuCategoryFixture, "   ", 1000, 50L, "image"));

				// then
				assertExceptionAndErrorCode(thrown, expected);
			}

		}

		@Nested
		@DisplayName("음식 상품 재고 개수가")
		class StockCount {
			@Test
			@DisplayName("[Exception] Null 이면 InvalidMenuCreationException 이 발생한다")
			void isNull() {
				// given
				ErrorCode expected = MenuErrorCode.NULL_EXIST;

				// when
				Throwable thrown = catchThrowable(
					() -> new Menu(storeFixture, menuCategoryFixture, "메뉴이름", 1000, null, "image"));

				// then
				assertExceptionAndErrorCode(thrown, expected);
			}

			@Test
			@DisplayName("[Exception] 음수면 InvalidMenuCreationException 이 발생한다")
			void isEmpty() {
				// given
				ErrorCode expected = MenuErrorCode.INVALID_STOCK_COUNT;

				// when
				Throwable thrown = catchThrowable(
					() -> new Menu(storeFixture, menuCategoryFixture, "메뉴이름", 1000, -1L, "image"));

				// then
				assertExceptionAndErrorCode(thrown, expected);
			}

			@Test
			@DisplayName("[Exception] 0개면 InvalidMenuCreationException 이 발생한다")
			void isBlank() {
				// given
				ErrorCode expected = MenuErrorCode.INVALID_STOCK_COUNT;

				// when
				Throwable thrown = catchThrowable(
					() -> new Menu(storeFixture, menuCategoryFixture, "메뉴이름", 1000, 0L, "image"));

				// then
				assertExceptionAndErrorCode(thrown, expected);
			}
		}

	}

	@Nested
	@DisplayName("메뉴 가격 업데이트는")
	class UpdatePriceTest {
		private int menuPrice = 10000;
		private Menu menu = new Menu(storeFixture, menuCategoryFixture, "메뉴1", menuPrice, 10L, "imageUrl");

		@Nested
		@DisplayName("업데이트 하려는 가격이")
		class UpdatePrice {
			@Test
			@DisplayName("[Exception] 음수이면 InvalidMenuPriceUpdateException이 발생한다.")
			void negativeUpdatePrice() {
				//given
				int updatePrice = -1;

				//when & then
				HttpStatusException throwable = (HttpStatusException)catchThrowable(
					() -> menu.updatePrice(updatePrice));
				assertThat(throwable).isInstanceOf(InvalidMenuPriceUpdateException.class);
				assertThat(throwable.errorCode()).isEqualTo(MenuErrorCode.INVALID_PRICE);
			}

			@Test
			@DisplayName("[Exception] 0이면 InvalidMenuPriceUpdateException이 발생한다.")
			void zeroUpdatePrice() {
				//given
				int updatePrice = 0;

				//when & then
				HttpStatusException throwable = (HttpStatusException)catchThrowable(
					() -> menu.updatePrice(updatePrice));
				assertThat(throwable).isInstanceOf(InvalidMenuPriceUpdateException.class);
				assertThat(throwable.errorCode()).isEqualTo(MenuErrorCode.INVALID_PRICE);
			}

			@Test
			@DisplayName("[success] 양수면 가격이 update되고 update된 가격이 return된다.")
			void positiveUpdatePrice() {
				//given
				int updatePrice = 9000;

				//when
				int updatedPrice = menu.updatePrice(updatePrice);

				//then
				assertThat(updatedPrice).isEqualTo(updatePrice);
				assertThat(menu.getPrice()).isEqualTo(updatePrice);
			}
		}
	}

	private void assertExceptionAndErrorCode(Throwable thrown, ErrorCode expected) {
		assertThat(thrown).isInstanceOf(InvalidMenuCreationException.class);
		InvalidMenuCreationException exception = (InvalidMenuCreationException)thrown;
		assertThat(exception.errorCode()).isEqualTo(expected);
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