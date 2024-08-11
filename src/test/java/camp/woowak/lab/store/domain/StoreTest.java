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

		@Nested
		@DisplayName("가게 이용 시간이")
		class StoreTime {

			@Test
			@DisplayName("[Success] 시작 시간이 종료 시간보다 이전이다")
			void storeStartTimeBeforeThanEndTime() {
				// given
				LocalDateTime validStartTime = validStartTimeFixture;
				LocalDateTime validEndTime = validEndTimeFixture;

				// when & then
				assertThatCode(
					() -> new Store(null, null, validNameFixture, validAddressFixture, null, validMinOrderPriceFixture,
						validStartTime, validEndTime))
					.doesNotThrowAnyException();
			}

			@Test
			@DisplayName("[Exception] 종료 시간이 시작 시간과 같으면 StoreException 이 발생한다")
			void endTimeSameWithStartTime() {
				// given
				LocalDateTime endTimeSameWithStartTime = fixedStartTime.now();

				// when & then
				assertThatThrownBy(
					() -> new Store(null, null, validNameFixture, validAddressFixture, null, validMinOrderPriceFixture,
						validStartTimeFixture, endTimeSameWithStartTime))
					.isInstanceOf(StoreException.class);
			}

			@Test
			@DisplayName("[Exception] 종료 시간이 시작 시간 이전이면 StoreException 이 발생한다")
			void endTimeBeforeThanStartTime() {
				// given
				LocalDateTime endTimeBeforeThanStartTime = validStartTimeFixture.minusMinutes(1);

				// when & then
				assertThatThrownBy(
					() -> new Store(null, null, validNameFixture, validAddressFixture, null, validMinOrderPriceFixture,
						validStartTimeFixture, endTimeBeforeThanStartTime))
					.isInstanceOf(StoreException.class);
			}

			@Test
			@DisplayName("[Success] 시작 시간과 종료 시간의 단위는 분(m) 단위 까지 가능하다")
			void validStartTimeUnit() {
				// given
				LocalDateTime validStartTimeUnitMinute = validStartTimeFixture;
				LocalDateTime validEndTimeUnitMinute = validEndTimeFixture;

				// when & then
				assertThatCode(
					() -> new Store(null, null, validNameFixture, validAddressFixture, null, validMinOrderPriceFixture,
						validStartTimeUnitMinute, validEndTimeUnitMinute))
					.doesNotThrowAnyException();
			}

			@Test
			@DisplayName("[Exception] 시작 시간에 초(s) 단위가 포함되면 StoreException 이 발생한다")
			void startTimeWithSeconds() {
				// given
				DateTimeProvider inValidUnitDateTimeProvider = () -> LocalDateTime.of(2024, 8, 24, 10, 10, 1);
				LocalDateTime startTimeWithSeconds = inValidUnitDateTimeProvider.now();

				// when & then
				assertThatThrownBy(
					() -> new Store(null, null, validNameFixture, validAddressFixture, null, validMinOrderPriceFixture,
						startTimeWithSeconds, validEndTimeFixture))
					.isInstanceOf(StoreException.class);
			}

			@Test
			@DisplayName("[Exception] 시작 시간에 나노초(ms) 단위가 포함되면 StoreException 이 발생한다")
			void startTimeWithNanoSeconds() {
				// given
				DateTimeProvider inValidUnitDateTimeProvider = () -> LocalDateTime.of(2024, 8, 24, 10, 0, 0, 1);
				LocalDateTime startTimeWithNanoSeconds = inValidUnitDateTimeProvider.now();

				// when & then
				assertThatThrownBy(() -> new Store(null, null, validNameFixture, validAddressFixture, null, 5000,
					startTimeWithNanoSeconds, validEndTimeFixture))
					.isInstanceOf(StoreException.class);
			}

			@Test
			@DisplayName("[Exception] 종료 시간에 초(s) 단위가 포함되면 StoreException 이 발생한다")
			void endTimeWithSeconds() {
				// given
				DateTimeProvider inValidUnitDateTimeProvider = () -> LocalDateTime.of(2024, 8, 24, 10, 30, 1);
				LocalDateTime endTimeWithSeconds = inValidUnitDateTimeProvider.now();

				// when & then
				assertThatThrownBy(() -> new Store(null, null, validNameFixture, validAddressFixture, null, 5000,
					validStartTimeFixture, endTimeWithSeconds))
					.isInstanceOf(StoreException.class);
			}

			@Test
			@DisplayName("[Exception] 종료 시간에 나노초(ms) 단위가 포함되면 StoreException 이 발생한다")
			void endTimeWithNanoSeconds() {
				// given
				DateTimeProvider inValidUnitDateTimeProvider = () -> LocalDateTime.of(2024, 8, 24, 10, 30, 0, 1);
				LocalDateTime endTimeWithNanoSeconds = inValidUnitDateTimeProvider.now();

				// when & then
				assertThatThrownBy(() -> new Store(null, null, validNameFixture, validAddressFixture, null, 5000,
					validStartTimeFixture, endTimeWithNanoSeconds))
					.isInstanceOf(StoreException.class);
			}

		}

	}

}