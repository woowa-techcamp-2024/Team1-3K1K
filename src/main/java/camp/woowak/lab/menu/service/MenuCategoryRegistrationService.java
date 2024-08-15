package camp.woowak.lab.menu.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.menu.exception.DuplicateMenuCategoryException;
import camp.woowak.lab.menu.exception.UnauthorizedMenuCategoryCreationException;
import camp.woowak.lab.menu.repository.MenuCategoryRepository;
import camp.woowak.lab.menu.service.command.MenuCategoryRegistrationCommand;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import camp.woowak.lab.store.repository.StoreRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class MenuCategoryRegistrationService {

	private final StoreRepository storeRepository;
	private final MenuCategoryRepository menuCategoryRepository;

	public MenuCategoryRegistrationService(StoreRepository storeRepository,
										   MenuCategoryRepository menuCategoryRepository) {
		this.storeRepository = storeRepository;
		this.menuCategoryRepository = menuCategoryRepository;
	}

	public Long register(MenuCategoryRegistrationCommand cmd) {
		Optional<Store> findStore = storeRepository.findById(cmd.storeId());
		if (findStore.isEmpty()) {
			log.info("등록되지 않은 점포 {}에 메뉴 카테고리 등록을 시도했습니다.", cmd.storeId());
			throw new NotFoundStoreException("등록되지 않는 Store에 메뉴 카테고리 생성을 시도했습니다.");
		}
		Store store = findStore.get();
		if (!store.isOwnedBy(cmd.vendorId())) {
			log.info("권한없는 사용자 {}가 점포 {}에 메뉴 카테고리 등록을 시도했습니다.", cmd.vendorId(), cmd.storeId());
			throw new UnauthorizedMenuCategoryCreationException("권한없는 사용자가 메뉴 카테고리 등록을 시도했습니다.");
		}
		MenuCategory savedMenuCategory;
		try {
			savedMenuCategory = menuCategoryRepository.saveAndFlush(new MenuCategory(store, cmd.name()));
		} catch (DataIntegrityViolationException e) {
			log.info("점주 {}가 점포 {}에 중복된 이름의 메뉴 카테고리 등록을 시도했습니다.", cmd.vendorId(), cmd.storeId());
			throw new DuplicateMenuCategoryException("같은 이름의 메뉴 카테고리 생성을 시도했습니다.");
		}
		return savedMenuCategory.getId();
	}
}
