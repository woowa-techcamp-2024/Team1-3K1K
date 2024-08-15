package camp.woowak.lab.cart.service;

import org.springframework.stereotype.Service;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.exception.MenuNotFoundException;
import camp.woowak.lab.cart.repository.CartRepository;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.repository.MenuRepository;

@Service
public class CartService {
	private final CartRepository cartRepository;
	private final MenuRepository menuRepository;

	public CartService(CartRepository cartRepository, MenuRepository menuRepository) {
		this.cartRepository = cartRepository;
		this.menuRepository = menuRepository;
	}

	/**
	 * @throws MenuNotFoundException                                  메뉴가 존재하지 않는 경우
	 * @throws camp.woowak.lab.cart.exception.OtherStoreMenuException 다른 가게의 메뉴를 담은 경우 도메인에서 발생
	 * @throws camp.woowak.lab.cart.exception.StoreNotOpenException   해당 가게가 열려있지 않은 경우 발생
	 */
	public void addMenu(Long customerId, Long menuId) {
		Cart customerCart = getCart(customerId);

		Menu menu = menuRepository.findByIdWithStore(menuId)
			.orElseThrow(() -> new MenuNotFoundException(menuId + " not found"));

		customerCart.addMenu(menu);
	}

	private Cart getCart(Long customerId) {
		return cartRepository.findByCustomerId(customerId)
			.orElse(cartRepository.save(new Cart(customerId)));
	}
}
