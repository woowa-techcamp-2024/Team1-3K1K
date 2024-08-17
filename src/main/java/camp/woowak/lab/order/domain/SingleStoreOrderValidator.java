package camp.woowak.lab.order.domain;

import java.util.List;

import org.springframework.stereotype.Component;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.order.exception.EmptyCartException;
import camp.woowak.lab.order.exception.MultiStoreOrderException;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import camp.woowak.lab.store.repository.StoreRepository;

@Component
public class SingleStoreOrderValidator {
	private final StoreRepository storeRepository;

	public SingleStoreOrderValidator(StoreRepository storeRepository) {
		this.storeRepository = storeRepository;
	}

	public Store check(List<CartItem> cartItems) {
		if (cartItems == null || cartItems.isEmpty()) {
			throw new EmptyCartException("최소 하나 이상의 메뉴를 주문해야 합니다.");
		}

		Store store = storeRepository.findById(cartItems.get(0).getStoreId())
			.orElseThrow(() -> new NotFoundStoreException("등록되지 않은 가게의 상품을 주문했습니다."));

		for (CartItem cartItem : cartItems) {
			if (!cartItem.getStoreId().equals(store.getId())) {
				throw new MultiStoreOrderException("다른 가게의 메뉴를 같이 주문할 수 없습니다.");
			}
		}

		return store;
	}
}
