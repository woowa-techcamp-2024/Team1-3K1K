package camp.woowak.lab.store.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.menu.exception.NotFoundMenuCategoryException;
import camp.woowak.lab.menu.repository.MenuCategoryRepository;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.store.service.command.MenuLineItem;
import camp.woowak.lab.store.service.command.StoreMenuRegistrationCommand;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.exception.NotFoundVendorException;
import camp.woowak.lab.vendor.repository.VendorRepository;
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

	private final VendorRepository vendorRepository;
	private final StoreRepository storeRepository;
	private final MenuRepository menuRepository;
	private final MenuCategoryRepository menuCategoryRepository;

	/**
	 *
	 * @throws NotFoundMenuCategoryException 가게와 메뉴카테고리 이름으로 메뉴카테고리를 찾지 못할 때 발생
	 *
	 */
	@Transactional
	public List<Long> storeMenuRegistration(final StoreMenuRegistrationCommand command) {
		Vendor owner = findVendor(command.vendorId());

		Store store = findStoreBy(command.storeId());
		store.validateOwner(owner);

		List<MenuLineItem> menuLineItems = command.menuItems();
		List<Menu> menus = createMenus(store, menuLineItems);

		List<Menu> menuIds = menuRepository.saveAll(menus);

		return menuIds.stream()
			.map(Menu::getId)
			.toList();
	}

	private Vendor findVendor(final UUID vendorId) {
		return vendorRepository.findById(vendorId)
			.orElseThrow(NotFoundVendorException::new);
	}

	private Store findStoreBy(final Long storeId) {
		return storeRepository.findById(storeId)
			.orElseThrow(() -> new NotFoundStoreException("존재하지 않는 가게입니다."));
	}

	private List<Menu> createMenus(final Store store,
								   final List<MenuLineItem> menuLineItems
	) {
		return menuLineItems.stream()
			.map(menuLineItem -> createMenu(store, menuLineItem))
			.toList();
	}

	private Menu createMenu(final Store store, final MenuLineItem menuLineItem) {
		MenuCategory menuCategory = findMenuCategoryBy(store, menuLineItem.categoryName());
		return new Menu(store, menuCategory, menuLineItem.name(), menuLineItem.price(), menuLineItem.imageUrl());
	}

	private MenuCategory findMenuCategoryBy(final Store store, final String manuCategoryName) {
		return menuCategoryRepository.findByStoreIdAndName(store.getId(), manuCategoryName)
			.orElseThrow(() -> new NotFoundMenuCategoryException(store + ", " + manuCategoryName + " 의 메뉴카테고리가 없습니다."));
	}

}
