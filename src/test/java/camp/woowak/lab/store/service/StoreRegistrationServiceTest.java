package camp.woowak.lab.store.service;

import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import camp.woowak.lab.infra.date.DateTimeProvider;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.domain.TestPayAccount;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.exception.NotFoundStoreCategoryException;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.store.service.command.StoreRegistrationCommand;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class StoreRegistrationServiceTest {

	@Mock
	private VendorRepository vendorRepository;

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
		Vendor vendor = createVendor();
		UUID id = UUID.randomUUID();

		String storeCategoryName = "한식";
		StoreRegistrationCommand request = createStoreRegistrationRequest(id, storeCategoryName);
		StoreCategory mockStoreCategory = new StoreCategory(storeCategoryName);

		when(vendorRepository.findById(id)).thenReturn(Optional.of(vendor));
		when(storeCategoryRepository.findByName(storeCategoryName)).thenReturn(Optional.of(mockStoreCategory));
		when(storeRepository.save(any(Store.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// when
		storeRegistrationService.storeRegistration(request);

		// then
		then(storeRepository).should().save(any(Store.class));
	}

	@Test
	@DisplayName("[Exception] 유효하지 않은 가게 카테고리면 예외가 발생한다.")
	void notExistStoreCategoryName() {
		// given
		Vendor vendor = createVendor();
		UUID id = UUID.randomUUID();

		String invalidCategoryName = "존재하지 않는 카테고리";
		StoreRegistrationCommand request = createStoreRegistrationRequest(id, invalidCategoryName);

		when(vendorRepository.findById(id)).thenReturn(Optional.of(vendor));
		given(storeCategoryRepository.findByName(invalidCategoryName))
			.willReturn(Optional.empty());

		// when & then
		Assertions.assertThatThrownBy(() -> storeRegistrationService.storeRegistration(request))
			.isInstanceOf(NotFoundStoreCategoryException.class);

		then(storeRepository).shouldHaveNoInteractions();
	}

	private StoreRegistrationCommand createStoreRegistrationRequest(UUID id, String storeCategory) {
		return new StoreRegistrationCommand(id, "3K1K가게", "송파", "02-0000-0000",
			storeCategory, 5000, validStartTimeFixture, validEndTimeFixture);
	}

	private Vendor createVendor() {
		PayAccount payAccount = new TestPayAccount(1L);
		PasswordEncoder passwordEncoder = new NoOpPasswordEncoder();
		return new Vendor("vendorName", "vendorEmail@example.com", "vendorPassword", "010-0000-0000", payAccount,
			passwordEncoder);
	}

}