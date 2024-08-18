package camp.woowak.lab.menu.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.cart.exception.MenuNotFoundException;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.exception.InvalidMenuStockUpdateException;
import camp.woowak.lab.menu.exception.NotEqualsOwnerException;
import camp.woowak.lab.menu.exception.StoreNotOpenException;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.menu.service.command.UpdateMenuStockCommand;

@Service
public class UpdateMenuStockService {
	private final MenuRepository menuRepository;

	public UpdateMenuStockService(MenuRepository menuRepository) {
		this.menuRepository = menuRepository;
	}

	/**
	 *
	 * @throws MenuNotFoundException 메뉴를 찾을 수 없는 경우 발생한다.
	 * @throws NotEqualsOwnerException 메뉴를 소유한 가게의 주인이 아닌 경우 발생한다.
	 * @throws StoreNotOpenException 가게가 열려있지 않은 경우 발생한다.
	 * @throws InvalidMenuStockUpdateException 메뉴의 재고를 변경할 수 없는 경우 발생한다.
	 */
	@Transactional
	public Long updateMenuStock(UpdateMenuStockCommand cmd) {
		// 수량을 변경하려는 메뉴를 조회한다.
		Menu targetMenu = findMenuByIdForUpdateOrThrow(cmd.menuId());

		// 메뉴를 소유한 가게를 조회한다.
		if (!targetMenu.getStore().isOwnedBy(cmd.vendorId())) {
			throw new NotEqualsOwnerException("메뉴를 소유한 가게의 주인이 아닙니다.");
		}

		// 가게가 열려있는지 확인한다.
		if (!targetMenu.getStore().isOpen()) {
			throw new StoreNotOpenException("가게가 열려있지 않습니다.");
		}

		// 메뉴의 재고를 변경한다.
		int modifiedRow = menuRepository.updateStock(cmd.menuId(), cmd.stock());
		if (modifiedRow != 1) { // 변경된 메뉴의 개수가 1이 아닌 경우 예외를 발생시킨다.
			throw new InvalidMenuStockUpdateException("변경의 영향을 받은 메뉴의 개수가 1이 아닙니다.");
		}

		return targetMenu.getId();
	}

	private Menu findMenuByIdForUpdateOrThrow(Long menuId) {
		return menuRepository.findByIdForUpdate(menuId).orElseThrow(() -> new MenuNotFoundException("메뉴를 찾을 수 없습니다."));
	}
}
