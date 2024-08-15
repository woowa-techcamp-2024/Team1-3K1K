package camp.woowak.lab.store.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.exception.NotFoundStoreCategoryException;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.store.service.dto.StoreRegistrationRequest;
import camp.woowak.lab.vendor.domain.Vendor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreRegistrationService {

	private final StoreRepository storeRepository;
	private final StoreCategoryRepository storeCategoryRepository;

	/**
	 * @throws camp.woowak.lab.store.exception.InvalidStoreCreationException Store 객체 검증 실패
	 * @throws NotFoundStoreCategoryException 존재하지 않는 이름의 가게 카테고리
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
			request.storeStartTime(),
			request.storeEndTime());
	}

	private StoreCategory findStoreCategoryByName(final String name) {
		return storeCategoryRepository.findByName(name)
			.orElseThrow(() -> new NotFoundStoreCategoryException("해당 이름의 가게 카테고리가 없습니다. " + name));
	}

}
