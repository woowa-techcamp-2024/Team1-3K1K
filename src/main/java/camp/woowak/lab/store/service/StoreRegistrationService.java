package camp.woowak.lab.store.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.exception.NotFoundStoreCategoryException;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.store.service.command.StoreRegistrationCommand;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.exception.NotFoundVendorException;
import camp.woowak.lab.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreRegistrationService {

	private final VendorRepository vendorRepository;
	private final StoreRepository storeRepository;
	private final StoreCategoryRepository storeCategoryRepository;

	/**
	 * @throws camp.woowak.lab.store.exception.InvalidStoreCreationException Store 객체 생성 검증에 실패시
	 * @throws NotFoundStoreCategoryException 가게 카테고리 이름에 대한 가게 카테고리가 존재하지 않을 시
	 * @throws NotFoundVendorException UUID 에 대한 가게 점주가 존재하지 않을 시
	 */
	@Transactional
	public void storeRegistration(final StoreRegistrationCommand command) {
		final Vendor vendor = findVendor(command.vendorId());

		final StoreCategory storeCategory = findStoreCategoryByName(command.storeCategoryName());
		final Store store = createStore(vendor, storeCategory, command);

		storeRepository.save(store);
	}

	private Vendor findVendor(final UUID vendorId) {
		return vendorRepository.findById(vendorId)
			.orElseThrow(NotFoundVendorException::new);
	}

	private Store createStore(Vendor vendor, StoreCategory storeCategory, StoreRegistrationCommand command) {
		return new Store(vendor, storeCategory, command.storeName(),
			command.storeAddress(),
			command.storePhoneNumber(),
			command.storeMinOrderPrice(),
			command.storeStartTime(),
			command.storeEndTime());
	}

	private StoreCategory findStoreCategoryByName(final String name) {
		return storeCategoryRepository.findByName(name)
			.orElseThrow(() -> new NotFoundStoreCategoryException("해당 이름의 가게 카테고리가 없습니다. " + name));
	}

}
