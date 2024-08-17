package camp.woowak.lab.store.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.store.service.response.StoreDisplayResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreDisplayService {

	private final StoreRepository storeRepository;
	private final MenuRepository menuRepository;

	@Transactional(readOnly = true)
	public StoreDisplayResponse findStore(final Long storeId) {
		Store store = findStoreById(storeId);
		List<Menu> storeMenus = findMenusByStore(store.getId());

		return StoreDisplayResponse.of(store, mapFrom(storeMenus));
	}

	private Store findStoreById(final Long storeId) {
		return storeRepository.findById(storeId)
			.orElseThrow(() -> new NotFoundStoreException(storeId + "의 가게를 찾을 수 없습니다."));
	}

	private List<Menu> findMenusByStore(final Long storeId) {
		return menuRepository.findByStoreId(storeId);
	}

	private List<StoreDisplayResponse.MenuDisplayResponse> mapFrom(final List<Menu> menus) {
		return menus.stream()
			.map(StoreDisplayResponse.MenuDisplayResponse::of)
			.toList();
	}

}
