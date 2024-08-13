package camp.woowak.lab.store.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import camp.woowak.lab.infra.date.DateTimeProvider;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreAddress;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;

@DataJpaTest
class StoreRepositoryTest {

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private StoreCategoryRepository storeCategoryRepository;

	DateTimeProvider fixedStartTime = () -> LocalDateTime.of(2024, 8, 24, 1, 0, 0);
	DateTimeProvider fixedEndTime = () -> LocalDateTime.of(2024, 8, 24, 5, 0, 0);

	LocalDateTime validStartTimeFixture = fixedStartTime.now();
	LocalDateTime validEndTimeFixture = fixedEndTime.now();

	String validNameFixture = "3K1K 가게";
	String validAddressFixture = StoreAddress.DEFAULT_DISTRICT;
	String validPhoneNumberFixture = "02-0000-0000";
	Integer validMinOrderPriceFixture = 5000;

	@Nested
	@DisplayName("가게를 저장하는 기능은")
	class SaveStore {

		@Test
		@DisplayName("[Success] 가게 저장 성공")
		void successfulSaveStore() {
			// given
			Vendor vendor = new Vendor();
			vendorRepository.saveAndFlush(vendor);

			StoreCategory storeCategory = new StoreCategory("한식");
			storeCategoryRepository.saveAndFlush(storeCategory);

			Store store = createStore(vendor, storeCategory);

			// when & then
			assertThatCode(() -> storeRepository.save(store))
				.doesNotThrowAnyException();
		}

		@Test
		@DisplayName("[Exception] 참조중인 엔티티가 저장되지 않았고, Not Null 제약조건이면 InvalidDataAccessApiUsageException - Not-null property 가 발생한다")
		void test() {
			// given
			Vendor notSavedVendor = new Vendor();

			StoreCategory storeCategory = new StoreCategory("한식");
			storeCategoryRepository.saveAndFlush(storeCategory);

			// when
			Store store = createStore(notSavedVendor, storeCategory);

			// then
			assertThatThrownBy(() -> storeRepository.saveAndFlush(store))
				.isInstanceOf(InvalidDataAccessApiUsageException.class);
		}

	}

	@Nested
	@DisplayName("점주 ID로 가게를 조회하는 기능은")
	class FindByOwnerId {

		@Test
		@DisplayName("[Success] 점주가 존재하는 경우 조회를 성공한다")
		void success() {
			// given
			Vendor vendor = new Vendor();
			vendorRepository.saveAndFlush(vendor);

			StoreCategory storeCategory = new StoreCategory("한식");
			storeCategoryRepository.saveAndFlush(storeCategory);

			Store store = createStore(vendor, storeCategory);
			storeRepository.saveAndFlush(store);

			// when & then
			assertThat(storeRepository.findByOwnerId(vendor.getId()))
				.isPresent()
				.containsSame(store);
		}

		@Test
		@DisplayName("[Exception] 점주가 존재하지 않는 경우 빈 Optional 을 반환한다")
		void notExistVendor() {
			// given
			Vendor vendor = new Vendor();
			vendorRepository.saveAndFlush(vendor);
			Vendor notSavedVendor = new Vendor();

			StoreCategory storeCategory = new StoreCategory("한식");
			storeCategoryRepository.saveAndFlush(storeCategory);

			Store store = createStore(vendor, storeCategory);
			storeRepository.saveAndFlush(store);

			// when & then
			assertThat(storeRepository.findByOwnerId(notSavedVendor.getId()))
				.isEmpty();
		}

	}

	private Store createStore(Vendor vendor, StoreCategory storeCategory) {
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