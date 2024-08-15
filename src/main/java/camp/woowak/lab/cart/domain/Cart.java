package camp.woowak.lab.cart.domain;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import camp.woowak.lab.cart.exception.OtherStoreMenuException;
import camp.woowak.lab.cart.exception.StoreNotOpenException;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreTime;
import lombok.Getter;

@Getter
public class Cart {
	private final String customerId;
	private final List<Menu> menuList;

	/**
	 * 생성될 때 무조건 cart가 비어있도록 구현
	 *
	 * @param customerId 장바구니 소유주의 ID값입니다.
	 */
	public Cart(String customerId) {
		this(customerId, new LinkedList<>());
	}

	/**
	 * 해당 Domain을 사용하는 같은 패키지내의 클래스, 혹은 자식 클래스는 List를 커스텀할 수 있습니다.
	 *
	 * @param customerId 장바구니 소유주의 ID값입니다.
	 * @param menuList 장바구니에 사용될 List입니다.
	 */
	protected Cart(String customerId, List<Menu> menuList) {
		this.customerId = customerId;
		this.menuList = menuList;
	}

	public void addMenu(Menu menu) {
		Store store = menu.getStore();
		validateOtherStore(store.getId());
		validateStoreOpenTime(store);

		this.menuList.add(menu);
	}

	public long totalPrice() {
		return this.menuList.stream()
			.map(Menu::getPrice)
			.mapToLong(Long::valueOf)
			.boxed()
			.reduce(0L, Long::sum);
	}

	private void validateStoreOpenTime(Store store) {
		if (!store.isOpen()) {
			StoreTime storeTime = store.getStoreTime();
			LocalDateTime openTime = storeTime.getStartTime();
			LocalDateTime closeTime = storeTime.getEndTime();
			LocalDateTime now = LocalDateTime.now();
			throw new StoreNotOpenException(
				"store does not open in " + now + ", workingTime = " + openTime + " ~ " + closeTime);
		}
	}

	private void validateOtherStore(Long menuStoreId) {
		Set<Long> storeIds = getStoreIds();
		if (!(storeIds.isEmpty() || storeIds.contains(menuStoreId)))
			throw new OtherStoreMenuException("can not add other store. storeId = " + menuStoreId);
	}

	private Set<Long> getStoreIds() {
		return this.menuList.stream()
			.map(Menu::getStore)
			.mapToLong(Store::getId)
			.boxed()
			.collect(Collectors.toSet());
	}
}
