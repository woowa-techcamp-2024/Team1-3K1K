package camp.woowak.lab.web.dao.store;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreAddress;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;

@Transactional
public class StoreDummiesFixture {
	protected final StoreRepository storeRepository;
	protected final StoreCategoryRepository storeCategoryRepository;
	protected final VendorRepository vendorRepository;
	protected final PayAccountRepository payAccountRepository;

	public StoreDummiesFixture(StoreRepository storeRepository, StoreCategoryRepository storeCategoryRepository,
							   VendorRepository vendorRepository, PayAccountRepository payAccountRepository) {
		System.out.println("created!");
		this.storeRepository = storeRepository;
		this.storeCategoryRepository = storeCategoryRepository;
		this.vendorRepository = vendorRepository;
		this.payAccountRepository = payAccountRepository;
	}

	protected List<Store> createDummyStores(int numberOfStores) {
		List<Store> stores = new ArrayList<>();

		Vendor vendor = createDummyVendor();
		for (int i = 0; i < numberOfStores; i++) {
			StoreCategory storeCategory = createRandomDummyStoreCategory();
			String name = "Store " + (i + 1);
			String address = StoreAddress.DEFAULT_DISTRICT;
			String phoneNumber = "123-456-789" + (i % 10);
			Integer minOrderPrice = 5000 + (new Random().nextInt(10000)) / 1000 * 1000;
			LocalDateTime startTime = LocalDateTime.now().plusHours(new Random().nextInt(10)).withSecond(0).withNano(0);
			LocalDateTime endTime = startTime.plusHours(new Random().nextInt(20) + 1);

			Store store = new Store(vendor, storeCategory, name, address, phoneNumber, minOrderPrice, startTime,
									endTime);
			stores.add(store);
		}

		storeRepository.saveAllAndFlush(stores);
		return stores;
	}

	protected Vendor createDummyVendor() {
		PayAccount payAccount = new PayAccount();
		payAccountRepository.saveAndFlush(payAccount);
		return vendorRepository.saveAndFlush(
			new Vendor("VendorName", "email@gmail.com", "Password123!", "010-1234-5678",
					   payAccount, new NoOpPasswordEncoder()));
	}

	protected StoreCategory createRandomDummyStoreCategory() {
		return storeCategoryRepository.saveAndFlush(new StoreCategory(UUID.randomUUID().toString()));
	}
}
