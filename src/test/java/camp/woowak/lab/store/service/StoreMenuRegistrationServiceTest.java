package camp.woowak.lab.store.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.menu.repository.MenuCategoryRepository;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.domain.TestPayAccount;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreAddress;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.store.service.dto.StoreMenuRegistrationRequest;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class StoreMenuRegistrationServiceTest {

	@Mock
	StoreRepository storeRepository;

	@Mock
	MenuRepository menuRepository;

	@Mock
	MenuCategoryRepository menuCategoryRepository;

	@InjectMocks
	StoreMenuRegistrationService storeMenuRegistrationService;

	Store storeFixture = createValidStore();

	MenuCategory menuCategoryFixture = createValidMenuCategory();

	@Test
	@DisplayName("[Success] 메뉴 등록 성공")
	void storeMenuRegistrationSuccess() {
		// given
		Vendor owner = createVendor();
		List<StoreMenuRegistrationRequest.MenuLineItem> menuItems = List.of(
			new StoreMenuRegistrationRequest.MenuLineItem("메뉴1", "image1.jpg", "카테고리1", 10000)
		);
		StoreMenuRegistrationRequest request = new StoreMenuRegistrationRequest(menuItems);

		when(storeRepository.findByOwnerId(owner.getId())).thenReturn(Optional.of(storeFixture));
		when(menuCategoryRepository.findByStoreIdAndName(storeFixture.getId(), "카테고리1")).thenReturn(
			Optional.of(menuCategoryFixture));

		// when
		storeMenuRegistrationService.storeMenuRegistration(owner, request);

		// then
		verify(storeRepository).findByOwnerId(owner.getId());
		verify(menuCategoryRepository).findByStoreIdAndName(storeFixture.getId(), "카테고리1");
		verify(menuRepository).saveAll(anyList());
	}

	@Test
	@DisplayName("[Exception] 존재하지 않는 가게")
	void storeMenuRegistrationStoreNotFound() {
		// given
		Vendor owner = createVendor();
		List<StoreMenuRegistrationRequest.MenuLineItem> menuItems = List.of(
			new StoreMenuRegistrationRequest.MenuLineItem("메뉴1", "image1.jpg", "카테고리1", 10000)
		);
		StoreMenuRegistrationRequest request = new StoreMenuRegistrationRequest(menuItems);

		when(storeRepository.findByOwnerId(owner.getId())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> storeMenuRegistrationService.storeMenuRegistration(owner, request))
			.isInstanceOf(NotFoundStoreException.class);
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

	private Vendor createVendor() {
		PayAccount payAccount = new TestPayAccount(1L);
		PasswordEncoder passwordEncoder = new NoOpPasswordEncoder();
		return new Vendor("vendorName", "vendorEmail@example.com", "vendorPassword", "010-0000-0000", payAccount,
			passwordEncoder);
	}
}