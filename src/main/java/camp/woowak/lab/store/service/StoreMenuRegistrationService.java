package camp.woowak.lab.store.service;

import java.util.List;

import org.springframework.stereotype.Service;

import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.menu.exception.NotFoundMenuCategoryException;
import camp.woowak.lab.menu.repository.MenuCategoryRepository;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.store.service.dto.StoreMenuRegistrationRequest;
import camp.woowak.lab.vendor.domain.Vendor;
import lombok.RequiredArgsConstructor;

/**
 * <h4> Description </h4>
 * 가게 메뉴 등록을 담당
 *
 * <h4> User Story </h4>
 * 점주는 음식 상품을 등록할 수 있다.
 */
@Service
@RequiredArgsConstructor
public class StoreMenuRegistrationService {

	private final StoreRepository storeRepository;
	private final MenuRepository menuRepository;
	private final MenuCategoryRepository menuCategoryRepository;

	public void storeMenuRegistration(final Vendor owner, final StoreMenuRegistrationRequest request) {
		Store store = findStoreBy(owner.getId());

		List<StoreMenuRegistrationRequest.MenuLineItem> menuLineItems = request.menuItems();
		List<Menu> menus = createMenus(store, menuLineItems);

		menuRepository.saveAll(menus);
	}

	private Store findStoreBy(final Long storeId) {
		return storeRepository.findById(storeId)
			.orElseThrow(() -> new NotFoundStoreException("존재하지 않는 가게입니다."));
	}

	private List<Menu> createMenus(final Store store,
								   final List<StoreMenuRegistrationRequest.MenuLineItem> menuLineItems
	) {
		return menuLineItems.stream()
			.map(menuLineItem -> createMenu(store, menuLineItem))
			.toList();
	}

	private Menu createMenu(final Store store, final StoreMenuRegistrationRequest.MenuLineItem menuLineItem) {
		MenuCategory menuCategory = findMenuCategoryBy(store, menuLineItem.categoryName());
		return new Menu(store, menuCategory, menuLineItem.name(), menuLineItem.price(), menuLineItem.imageUrl());
	}

	private MenuCategory findMenuCategoryBy(final Store store, final String manuCategoryName) {
		return menuCategoryRepository.findByStoreIdAndName(store.getId(), manuCategoryName)
			.orElseThrow(() -> new NotFoundMenuCategoryException(store + ", " + manuCategoryName + " 의 메뉴카테고리가 없습니다."));
	}

}
