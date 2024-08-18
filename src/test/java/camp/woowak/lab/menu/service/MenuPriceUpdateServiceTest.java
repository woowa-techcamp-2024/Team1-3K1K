package camp.woowak.lab.menu.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import camp.woowak.lab.fixture.MenuFixture;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.exception.InvalidMenuPriceUpdateException;
import camp.woowak.lab.menu.exception.UnauthorizedMenuCategoryCreationException;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.menu.service.command.MenuPriceUpdateCommand;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.vendor.domain.Vendor;

@Nested
@DisplayName("MenuPriceUpdateService 클래스")
@ExtendWith(MockitoExtension.class)
class MenuPriceUpdateServiceTest implements MenuFixture {
	@InjectMocks
	private MenuPriceUpdateService menuPriceUpdateService;

	@Mock
	private MenuRepository menuRepository;

	private Vendor vendor;
	private Vendor otherVendor;
	private Store store;
	private Menu menu;
	private int menuPrice = 10000;

	@BeforeEach
	void setUpDummies() {
		vendor = createVendor(UUID.randomUUID());
		otherVendor = createVendor(UUID.randomUUID());
		store = createStore(vendor);
		menu = createMenu(1L, store, createMenuCategory(1L, store, "카테고리1"), "메뉴1", menuPrice);
	}

	@Nested
	@DisplayName("메뉴가격을 업데이트 할 때")
	class UpdateMenu {
		@Test
		@DisplayName("[성공] 해당 메뉴의 price가 정상적으로 업데이트 된다.")
		void success() {
			//given
			int updatePrice = menuPrice + 1000;
			MenuPriceUpdateCommand command = new MenuPriceUpdateCommand(vendor.getId(), menu.getId(), updatePrice);

			when(menuRepository.findByIdWithStore(menu.getId())).thenReturn(Optional.of(menu));

			//when
			int updatedMenuPrice = menuPriceUpdateService.updateMenuPrice(command);

			//then
			assertThat(updatedMenuPrice).isEqualTo(updatePrice);
			assertThat(menu.getPrice()).isEqualTo(updatePrice);
		}

		@Test
		@DisplayName("[Exception] 다른 가게 사장님이 다른 가게의 메뉴 가격을 수정하려는 경우 UnauthorizedMenuCategoryCreationException 를 던진다.")
		void otherVendorUpdateOtherStorePriceTest() {
			//given
			int updatePrice = menuPrice + 1000;
			MenuPriceUpdateCommand command = new MenuPriceUpdateCommand(otherVendor.getId(), menu.getId(), updatePrice);

			when(menuRepository.findByIdWithStore(menu.getId())).thenReturn(Optional.of(menu));

			//when & then
			assertThatThrownBy(() -> menuPriceUpdateService.updateMenuPrice(command))
				.isExactlyInstanceOf(UnauthorizedMenuCategoryCreationException.class);
		}

		@Test
		@DisplayName("[Exception] 업데이트 하려는 가격이 0인경우 InvalidMenuPriceUpdateException을 던진다.")
		void invalidNegativePrice() {
			//given
			int updatePrice = -1;
			MenuPriceUpdateCommand command = new MenuPriceUpdateCommand(vendor.getId(), menu.getId(), updatePrice);

			when(menuRepository.findByIdWithStore(menu.getId())).thenReturn(Optional.of(menu));

			//when & then
			assertThatThrownBy(() -> menuPriceUpdateService.updateMenuPrice(command))
				.isExactlyInstanceOf(InvalidMenuPriceUpdateException.class);
		}

		@Test
		@DisplayName("[Exception] 업데이트 하려는 가격이 0인경우 InvalidMenuPriceUpdateException을 던진다.")
		void invalidZeroPrice() {
			//given
			int updatePrice = 0;
			MenuPriceUpdateCommand command = new MenuPriceUpdateCommand(vendor.getId(), menu.getId(), updatePrice);

			when(menuRepository.findByIdWithStore(menu.getId())).thenReturn(Optional.of(menu));

			//when & then
			assertThatThrownBy(() -> menuPriceUpdateService.updateMenuPrice(command))
				.isExactlyInstanceOf(InvalidMenuPriceUpdateException.class);
		}
	}
}