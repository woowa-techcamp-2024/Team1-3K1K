package camp.woowak.lab.menu.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.exception.UnauthorizedMenuCategoryCreationException;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.menu.service.command.MenuPriceUpdateCommand;
import camp.woowak.lab.order.exception.NotFoundMenuException;
import camp.woowak.lab.store.domain.Store;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MenuPriceUpdateService {
	private final MenuRepository menuRepository;

	public MenuPriceUpdateService(MenuRepository menuRepository) {
		this.menuRepository = menuRepository;
	}

	/**
	 * @throws camp.woowak.lab.menu.exception.InvalidMenuPriceUpdateException 업데이트 하려는 가격이 0 또는 음수인 경우
	 * @throws UnauthorizedMenuCategoryCreationException                      자신의 가게의 메뉴가 아닌 메뉴를 업데이트 하려는 경우
	 * @throws NotFoundMenuException                                          존재하지 않는 메뉴의 가격을 업데이트 하려는 경우
	 */
	@Transactional
	public int updateMenuPrice(MenuPriceUpdateCommand cmd) {
		Menu menu = menuRepository.findByIdWithStore(cmd.menuId())
			.orElseThrow(() -> {
				log.info("등록되지 않은 메뉴 {}의 가격 수정을 시도했습니다.", cmd.menuId());
				throw new NotFoundMenuException("등록되지 않은 Menu의 가격 수정을 시도했습니다.");
			});

		Store store = menu.getStore();
		if (!store.isOwnedBy(cmd.vendorId())) {
			log.info("권한없는 사용자 {}가 점포 {}의 메뉴 가격 수정을 시도했습니다.", cmd.vendorId(), store.getId());
			throw new UnauthorizedMenuCategoryCreationException("권한없는 사용자가 메뉴 가격 수정을 시도했습니다.");
		}

		int updatedPrice = menu.updatePrice(cmd.updatePrice());
		log.info("Store({}) 의 메뉴({}) 가격을 ({})로 수정했습니다.", store.getId(), menu.getId(), cmd.updatePrice());

		return updatedPrice;
	}
}
