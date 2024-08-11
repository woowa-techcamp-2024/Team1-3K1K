package camp.woowak.lab.store.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.infra.date.DateTimeProvider;
import camp.woowak.lab.store.exception.StoreException;

class StoreTest {

	@Nested
	@DisplayName("Store 생성은")
	class StoreValidateTest {

		DateTimeProvider fixedStartTime = () -> LocalDateTime.of(2024, 8, 24, 1, 0, 0);
		DateTimeProvider fixedEndTime = () -> LocalDateTime.of(2024, 8, 24, 5, 0, 0);

		LocalDateTime validStartTimeFixture = fixedStartTime.now();
		LocalDateTime validEndTimeFixture = fixedEndTime.now();

		String validNameFixture = "3K1K 가게";

		String validAddressFixture = StoreAddress.DEFAULT_DISTRICT;

		Integer validMinOrderPriceFixture = 5000;

		@Nested
		@DisplayName("가게 최소 주문 가격이")
		class StoreMinOrderPrice {

			@Test
			@DisplayName("[Success] 5,000원 이상 가능하다")
			void validMinOrderPrice() {
				// given
				int validMinOrderPrice = 5000;

				// when & then
				assertThatCode(() -> new Store(null, null, validNameFixture, validAddressFixture, null,
					validMinOrderPrice,
					validStartTimeFixture, validEndTimeFixture))
					.doesNotThrowAnyException();
			}

			@Test
			@DisplayName("[Exception] 5,000원 미만이면 StoreException 이 발생한다")
			void lessThanMinOrderPrice() {
				// given
				int lessThanMinOrderPrice = 4999;

				// when & then
				assertThatThrownBy(() -> new Store(null, null, validNameFixture, validAddressFixture, null,
					lessThanMinOrderPrice,
					validStartTimeFixture, validEndTimeFixture))
					.isInstanceOf(StoreException.class);
			}

			@Test
			@DisplayName("[Success] 1,000원 단위로 가능하다")
			void validUnitOrderPrice() {
				// given
				int validMinOrderPrice = 10000;

				// when & then
				assertThatCode(() -> new Store(null, null, validNameFixture, validAddressFixture, null,
					validMinOrderPrice,
					validStartTimeFixture, validEndTimeFixture))
					.doesNotThrowAnyException();
			}

			@Test
			@DisplayName("[Exception] 1,000원 단위가 아니면 StoreException 이 발생한다")
			void inValidUnitOrderPrice() {
				// given
				int inValidUnitOrderPrice = 5001;

				// when & then
				assertThatThrownBy(() -> new Store(null, null, validNameFixture, validAddressFixture, null,
					inValidUnitOrderPrice,
					validStartTimeFixture, validEndTimeFixture))
					.isInstanceOf(StoreException.class);
			}
		}

	}

}