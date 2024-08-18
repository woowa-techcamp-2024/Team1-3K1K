package camp.woowak.lab.store.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.store.service.command.MenuLineItem;
import camp.woowak.lab.store.service.command.StoreMenuRegistrationCommand;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class StoreMenuRegistrationServiceTest {

	@Mock
	VendorRepository vendorRepository;

	@Mock
	StoreRepository storeRepository;

	@Mock
	MenuRepository menuRepository;

	@Mock
	MenuCategoryRepository menuCategoryRepository;

	@InjectMocks
	StoreMenuRegistrationService storeMenuRegistrationService;

	Vendor owner = createVendor();
	Store storeFixture = createValidStore(owner);

	MenuCategory menuCategoryFixture = createValidMenuCategory();

	@Test
	@DisplayName("[Success] 메뉴 등록 성공")
	void storeMenuRegistrationSuccess() {
		// given
		Long storeId = 1L;
		UUID vendorId = UUID.randomUUID();
		List<MenuLineItem> menuItems = List.of(
			new MenuLineItem("메뉴1", 50L, "image1.jpg", "카테고리1", 10000L)
		);

		StoreMenuRegistrationCommand request = new StoreMenuRegistrationCommand(vendorId, storeId, menuItems);

		when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(owner));
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(storeFixture));
		when(menuCategoryRepository.findByStoreIdAndName(storeFixture.getId(), "카테고리1")).thenReturn(
			Optional.of(menuCategoryFixture));

		// when
		storeMenuRegistrationService.storeMenuRegistration(request);

		// then
		verify(storeRepository).findById(storeId);
		verify(menuCategoryRepository).findByStoreIdAndName(storeFixture.getId(), "카테고리1");
		verify(menuRepository).saveAll(anyList());
	}

	@Test
	@DisplayName("[Exception] 존재하지 않는 가게")
	void storeMenuRegistrationStoreNotFound() {
		// given
		Long storeId = 1L;
		UUID vendorId = UUID.randomUUID();
		List<MenuLineItem> menuItems = List.of(
			new MenuLineItem("메뉴1", 50L, "image1.jpg", "카테고리1", 10000L)
		);
		StoreMenuRegistrationCommand command = new StoreMenuRegistrationCommand(vendorId, storeId, menuItems);

		when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(owner));
		when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> storeMenuRegistrationService.storeMenuRegistration(command))
			.isInstanceOf(NotFoundStoreException.class);
	}

	private Store createValidStore(Vendor owner) {
		LocalDateTime validStartDateFixture = LocalDateTime.of(2020, 1, 1, 1, 1);
		LocalDateTime validEndDateFixture = LocalDateTime.of(2020, 1, 1, 2, 1);
		String validNameFixture = "3K1K 가게";
		String validAddressFixture = StoreAddress.DEFAULT_DISTRICT;
		String validPhoneNumberFixture = "02-1234-5678";
		Integer validMinOrderPriceFixture = 5000;

		return new Store(owner, createStoreCategory(), validNameFixture, validAddressFixture,
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
		return new Vendor("vendorName", "vendorEmail@example.com", "vendorPassword", "010-0000-0000", payAccount,
			passwordEncoder);
	}

	private StoreCategory createStoreCategory() {
		return new StoreCategory("양식");
	}
}