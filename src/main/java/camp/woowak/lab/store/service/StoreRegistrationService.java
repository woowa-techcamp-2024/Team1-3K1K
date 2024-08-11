package camp.woowak.lab.store.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.exception.StoreException;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreRegistrationService {

	private final StoreRepository storeRepository;
	private final StoreCategoryRepository storeCategoryRepository;

	/**
	 * @throws StoreException Store 객체 검증 실패, 존재하지 않는 이름의 가게 카테고리
	 */
	@Transactional
	public void storeRegistration(final Vendor vendor, final StoreRegistrationRequest request) {
		final StoreCategory storeCategory = findStoreCategoryByName(request.storeCategoryName());
		final Store store = createStore(vendor, storeCategory, request);

		storeRepository.save(store);
	}

	private Store createStore(Vendor vendor, StoreCategory storeCategory, StoreRegistrationRequest request) {
		return new Store(vendor, storeCategory, request.storeName(),
			request.storeAddress(),
			request.storePhoneNumber(),
			request.storeMinOrderPrice(),
			request.storeStarTime(),
			request.storeEndTime());
	}

	private StoreCategory findStoreCategoryByName(final String name) {
		return storeCategoryRepository.findByName(name)
			.orElseThrow(() -> new StoreException("존재하지 않는 가게 카테고리입니다."));
	}

}
