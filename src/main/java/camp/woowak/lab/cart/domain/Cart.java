package camp.woowak.lab.cart.domain;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.cart.exception.OtherStoreMenuException;
import camp.woowak.lab.cart.exception.StoreNotOpenException;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreTime;
import lombok.Getter;

@Getter
public class Cart {
	private final String customerId;
	private final List<CartItem> cartItems;

	/**
	 * 생성될 때 무조건 cart가 비어있도록 구현
	 *
	 * @param customerId 장바구니 소유주의 ID값입니다.
	 */
	public Cart(String customerId) {
		this(customerId, new LinkedList<>());
	}

	/**
	 * @param customerId 장바구니 소유주의 ID값입니다.
	 * @param cartItems   장바구니에 사용될 List입니다.
	 */
	public Cart(String customerId, List<CartItem> cartItems) {
		this.customerId = customerId;
		this.cartItems = cartItems;
	}

	public void addMenu(Menu menu) {
		addMenu(menu, 1);
	}

	public void addMenu(Menu menu, int amount) {
		Store store = menu.getStore();
		validateOtherStore(store.getId());
		validateStoreOpenTime(store);

		CartItem existingCartItem = getExistingCartItem(menu, store);
		if (existingCartItem != null) {
			CartItem updatedCartItem = existingCartItem.add(amount);
			cartItems.set(cartItems.indexOf(existingCartItem), updatedCartItem);
		} else {
			this.cartItems.add(new CartItem(menu.getId(), store.getId(), amount));
		}
	}

	private CartItem getExistingCartItem(Menu menu, Store store) {
		return cartItems.stream()
			.filter(item -> item.getMenuId().equals(menu.getId()) && item.getStoreId().equals(store.getId()))
			.findFirst()
			.orElse(null);
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
		return this.cartItems.stream()
			.map(CartItem::getStoreId)
			.collect(Collectors.toSet());
	}
}
