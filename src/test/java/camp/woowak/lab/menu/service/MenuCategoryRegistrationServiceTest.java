package camp.woowak.lab.menu.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import camp.woowak.lab.fixture.MenuFixture;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.menu.exception.DuplicateMenuCategoryException;
import camp.woowak.lab.menu.exception.UnauthorizedMenuCategoryCreationException;
import camp.woowak.lab.menu.repository.MenuCategoryRepository;
import camp.woowak.lab.menu.service.command.MenuCategoryRegistrationCommand;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.exception.DuplicateEmailException;

@ExtendWith(MockitoExtension.class)
class MenuCategoryRegistrationServiceTest implements MenuFixture {
	@InjectMocks
	private MenuCategoryRegistrationService service;
	@Mock
	private StoreRepository storeRepository;
	@Mock
	private MenuCategoryRepository menuCategoryRepository;

	@Test
	@DisplayName("[성공] 점주는 새로운 이름의 MenuCategory를 생성할 수 있다.")
	void success() throws DuplicateEmailException {
		// given
		UUID vendorId = UUID.randomUUID();
		Long storeId = new Random().nextLong();
		Long menuCategoryId = new Random().nextLong();

		Store store = createStore(storeId, createVendor(vendorId));
		MenuCategory menuCategory = createMenuCategory(menuCategoryId, store, "싱싱한 회가 왔어요");

		// when
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
		when(menuCategoryRepository.saveAndFlush(any(MenuCategory.class))).thenReturn(menuCategory);
		MenuCategoryRegistrationCommand command = new MenuCategoryRegistrationCommand(vendorId, storeId, "싱싱한 회가 왔어요");

		// then
		Long registeredId = service.register(command);
		then(menuCategoryRepository).should().saveAndFlush(any(MenuCategory.class));
		Assertions.assertEquals(menuCategory.getId(), registeredId);
	}

	@Test
	@DisplayName("[예외] 존재하지 않는 Store면 NotFoundStoreException")
	void failWithNotFoundStore() {
		// given

		// when
		when(storeRepository.findById(anyLong())).thenReturn(Optional.empty());
		MenuCategoryRegistrationCommand command = new MenuCategoryRegistrationCommand(UUID.randomUUID(),
			new Random().nextLong(), "newCategoryName");

		// then
		Assertions.assertThrows(NotFoundStoreException.class, () -> service.register(command));
	}

	@Test
	@DisplayName("[예외] 점주가 아닌 사용자가 MenuCategory 생성을 시도하면 UnauthorizedMenuCategoryCreationException")
	void failWithUnauthorized() {
		// given
		UUID vendorId = UUID.randomUUID();
		Long storeId = new Random().nextLong();

		Store store = createStore(storeId, createVendor(vendorId));

		// when
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
		UUID someUserId = UUID.randomUUID();
		while (someUserId.equals(vendorId)) {
			someUserId = UUID.randomUUID();
		}
		MenuCategoryRegistrationCommand command = new MenuCategoryRegistrationCommand(someUserId, storeId,
			"썩은 회가 왔어요");

		// then
		Assertions.assertThrows(UnauthorizedMenuCategoryCreationException.class, () -> service.register(command));
	}

	@Test
	@DisplayName("[예외] 이미 생성되어 있는 메뉴 카테고리와 이름이 동일한 경우 DuplicateMenuCategoryException")
	void failWithPasswordMismatch() {
		// given
		UUID vendorId = UUID.randomUUID();
		Long storeId = new Random().nextLong();

		Store store = createStore(storeId, createVendor(vendorId));

		// when
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
		when(menuCategoryRepository.saveAndFlush(any(MenuCategory.class))).thenThrow(
			DataIntegrityViolationException.class);
		MenuCategoryRegistrationCommand command = new MenuCategoryRegistrationCommand(vendorId, storeId, "싱싱한 회가 왔어요");

		// then
		Assertions.assertThrows(DuplicateMenuCategoryException.class, () -> service.register(command));
	}
}
