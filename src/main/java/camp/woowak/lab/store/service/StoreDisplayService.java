package camp.woowak.lab.store.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.store.service.response.StoreDisplayResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreDisplayService {

	private final StoreRepository storeRepository;

	@Transactional(readOnly = true)
	public StoreDisplayResponse findStore(final Long storeId) {
		Store store = findStoreById(storeId);
		return StoreDisplayResponse.of(store);
	}

	private Store findStoreById(final Long storeId) {
		return storeRepository.findById(storeId)
			.orElseThrow(() -> new NotFoundStoreException(storeId + "의 가게를 찾을 수 없습니다."));
	}

}
