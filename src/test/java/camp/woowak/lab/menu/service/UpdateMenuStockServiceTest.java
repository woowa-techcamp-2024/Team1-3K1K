package camp.woowak.lab.menu.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import camp.woowak.lab.cart.exception.MenuNotFoundException;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.exception.InvalidMenuStockUpdateException;
import camp.woowak.lab.menu.exception.NotEqualsOwnerException;
import camp.woowak.lab.menu.exception.NotUpdatableTimeException;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.menu.service.command.UpdateMenuStockCommand;
import camp.woowak.lab.store.domain.Store;

@ExtendWith(MockitoExtension.class)
class UpdateMenuStockServiceTest {
	@InjectMocks
	private UpdateMenuStockService updateMenuStockService;

	@Mock
	private MenuRepository menuRepository;

	@Test
	@DisplayName("메뉴 재고 업데이트 테스트 - 성공")
	void testUpdateMenuStock() {
		// given
		Long menuId = 1L;
		int stock = 10;
		UUID vendorId = UUID.randomUUID();
		Menu fakeMenu = Mockito.mock(Menu.class);
		Store fakeStore = Mockito.mock(Store.class);

		given(fakeMenu.getStore()).willReturn(fakeStore);
		given(fakeStore.isOwnedBy(any(UUID.class))).willReturn(true);
		given(menuRepository.findByIdForUpdate(anyLong())).willReturn(Optional.of(fakeMenu));
		given(fakeStore.isOpen()).willReturn(false);
		given(menuRepository.updateStock(anyLong(), anyInt())).willReturn(1);

		UpdateMenuStockCommand cmd = new UpdateMenuStockCommand(menuId, stock, vendorId);

		// when
		updateMenuStockService.updateMenuStock(cmd);

		// then
		verify(menuRepository, times(1)).findByIdForUpdate(anyLong());
		verify(fakeMenu, times(2)).getStore();
		verify(fakeStore, times(1)).isOwnedBy(any(UUID.class));
		verify(fakeStore, times(1)).isOpen();
		verify(menuRepository, times(1)).updateStock(anyLong(), anyInt());
	}

	@Test
	@DisplayName("메뉴 재고 업데이트 테스트 - 메뉴를 찾을 수 없는 경우")
	void testUpdateMenuStockNotFound() {
		// given
		Long menuId = 1L;
		int stock = 10;
		UUID vendorId = UUID.randomUUID();

		given(menuRepository.findByIdForUpdate(anyLong())).willReturn(Optional.empty());

		UpdateMenuStockCommand cmd = new UpdateMenuStockCommand(menuId, stock, vendorId);

		// when
		// then
		assertThrows(MenuNotFoundException.class, () -> updateMenuStockService.updateMenuStock(cmd));
		verify(menuRepository, times(1)).findByIdForUpdate(anyLong());
		verify(menuRepository, never()).updateStock(anyLong(), anyInt());
	}

	@Test
	@DisplayName("메뉴 재고 업데이트 테스트 - 메뉴를 소유한 가게의 주인이 아닌 경우")
	void testUpdateMenuStockNotEqualsOwner() {
		// given
		Long menuId = 1L;
		int stock = 10;
		UUID vendorId = UUID.randomUUID();
		Menu fakeMenu = Mockito.mock(Menu.class);
		Store fakeStore = Mockito.mock(Store.class);

		given(fakeMenu.getStore()).willReturn(fakeStore);
		given(fakeStore.isOwnedBy(any(UUID.class))).willReturn(false);
		given(menuRepository.findByIdForUpdate(anyLong())).willReturn(Optional.of(fakeMenu));

		UpdateMenuStockCommand cmd = new UpdateMenuStockCommand(menuId, stock, vendorId);

		// when
		// then
		assertThrows(NotEqualsOwnerException.class, () -> updateMenuStockService.updateMenuStock(cmd));
		verify(menuRepository, times(1)).findByIdForUpdate(anyLong());
		verify(fakeMenu, times(1)).getStore();
		verify(fakeStore, times(1)).isOwnedBy(any(UUID.class));
		verify(menuRepository, never()).updateStock(anyLong(), anyInt());
	}

	@Test
	@DisplayName("메뉴 재고 업데이트 테스트 - 매장이 열려있는 경우")
	void testUpdateMenuStockStoreNotOpen() {
		// given
		Long menuId = 1L;
		int stock = 10;
		UUID vendorId = UUID.randomUUID();
		Menu fakeMenu = Mockito.mock(Menu.class);
		Store fakeStore = Mockito.mock(Store.class);

		given(fakeMenu.getStore()).willReturn(fakeStore);
		given(fakeStore.isOwnedBy(any(UUID.class))).willReturn(true);
		given(fakeStore.isOpen()).willReturn(true);
		given(menuRepository.findByIdForUpdate(anyLong())).willReturn(Optional.of(fakeMenu));

		UpdateMenuStockCommand cmd = new UpdateMenuStockCommand(menuId, stock, vendorId);

		// when
		// then
		assertThrows(NotUpdatableTimeException.class, () -> updateMenuStockService.updateMenuStock(cmd));
		verify(menuRepository, times(1)).findByIdForUpdate(anyLong());
		verify(fakeMenu, times(2)).getStore();
		verify(fakeStore, times(1)).isOwnedBy(any(UUID.class));
		verify(fakeStore, times(1)).isOpen();
		verify(menuRepository, never()).updateStock(anyLong(), anyInt());
	}

	@Test
	@DisplayName("메뉴 재고 업데이트 테스트 - 메뉴의 재고를 변경할 수 없는 경우")
	void testUpdateMenuStockInvalid() {
		// given
		Long menuId = 1L;
		int stock = 10;
		UUID vendorId = UUID.randomUUID();
		Menu fakeMenu = Mockito.mock(Menu.class);
		Store fakeStore = Mockito.mock(Store.class);

		given(fakeMenu.getStore()).willReturn(fakeStore);
		given(fakeStore.isOwnedBy(any(UUID.class))).willReturn(true);
		given(fakeStore.isOpen()).willReturn(false);
		given(menuRepository.findByIdForUpdate(anyLong())).willReturn(Optional.of(fakeMenu));
		given(menuRepository.updateStock(anyLong(), anyInt())).willReturn(0);

		UpdateMenuStockCommand cmd = new UpdateMenuStockCommand(menuId, stock, vendorId);

		// when
		// then
		assertThrows(InvalidMenuStockUpdateException.class, () -> updateMenuStockService.updateMenuStock(cmd));
		verify(menuRepository, times(1)).findByIdForUpdate(anyLong());
		verify(fakeMenu, times(2)).getStore();
		verify(fakeStore, times(1)).isOwnedBy(any(UUID.class));
		verify(fakeStore, times(1)).isOpen();

		verify(menuRepository, times(1)).updateStock(anyLong(), anyInt());
	}
}
