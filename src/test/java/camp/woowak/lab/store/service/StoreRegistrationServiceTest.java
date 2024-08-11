package camp.woowak.lab.store.service;

import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import camp.woowak.lab.infra.date.DateTimeProvider;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.exception.StoreException;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.domain.Vendor;

@ExtendWith(MockitoExtension.class)
class StoreRegistrationServiceTest {

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private StoreCategoryRepository storeCategoryRepository;

	@InjectMocks
	private StoreRegistrationService storeRegistrationService;

	DateTimeProvider fixedStartTime = () -> LocalDateTime.of(2024, 8, 24, 1, 0, 0);
	DateTimeProvider fixedEndTime = () -> LocalDateTime.of(2024, 8, 24, 5, 0, 0);

	LocalDateTime validStartTimeFixture = fixedStartTime.now();
	LocalDateTime validEndTimeFixture = fixedEndTime.now();

	@Test
	@DisplayName("[Success] 가게가 저장된다.")
	void successfulRegistration() {
		// given
		Vendor vendor = new Vendor();
		String storeCategoryName = "한식";
		StoreRegistrationRequest request = createStoreRegistrationRequest(storeCategoryName);
		StoreCategory mockStoreCategory = new StoreCategory(storeCategoryName);

		when(storeCategoryRepository.findByName(storeCategoryName)).thenReturn(Optional.of(mockStoreCategory));
		when(storeRepository.save(any(Store.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// when
		storeRegistrationService.storeRegistration(vendor, request);

		// then
		then(storeRepository).should().save(any(Store.class));
	}

	@Test
	@DisplayName("[Exception] 유효하지 않은 가게 카테고리면 예외가 발생한다.")
	void notExistStoreCategoryName() {
		// given
		Vendor vendor = new Vendor();
		String invalidCategoryName = "존재하지 않는 카테고리";
		StoreRegistrationRequest request = createStoreRegistrationRequest(invalidCategoryName);

		given(storeCategoryRepository.findByName(invalidCategoryName))
			.willReturn(Optional.empty());

		// when & then
		Assertions.assertThatThrownBy(() -> storeRegistrationService.storeRegistration(vendor, request))
			.isInstanceOf(StoreException.class);

		then(storeRepository).shouldHaveNoInteractions();
	}

	private StoreRegistrationRequest createStoreRegistrationRequest(String storeCategory) {
		return new StoreRegistrationRequest("3K1K가게", "송파", "02-0000-0000",
			storeCategory, 5000, validStartTimeFixture, validEndTimeFixture);
	}

}