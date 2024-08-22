package camp.woowak.lab.cart.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.cart.exception.MenuNotFoundException;
import camp.woowak.lab.cart.repository.CartRepository;
import camp.woowak.lab.cart.service.command.AddCartCommand;
import camp.woowak.lab.cart.service.command.CartTotalPriceCommand;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.repository.MenuRepository;

@Service
@Transactional
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
	public void addMenu(AddCartCommand command) {
		Cart customerCart = getCart(command.customerId());

		Menu menu = menuRepository.findByIdWithStore(command.menuId())
			.orElseThrow(() -> new MenuNotFoundException(command.menuId() + " not found"));

		customerCart.addMenu(menu);
		cartRepository.save(customerCart);
	}

	public long getTotalPrice(CartTotalPriceCommand command) {
		Cart cart = getCart(command.customerId());
		List<Menu> findMenus = menuRepository.findAllById(
			cart.getCartItems().stream().map(CartItem::getMenuId).toList());
		long totalPrice = 0L;
		for (Menu findMenu : findMenus) {
			for (CartItem item : cart.getCartItems()) {
				if (item.getMenuId().equals(findMenu.getId())) {
					totalPrice += (long)findMenu.getPrice() * item.getAmount();
				}
			}
		}
		return totalPrice;
	}

	private Cart getCart(String customerId) {
		return cartRepository.findByCustomerId(customerId)
			.orElseGet(() -> cartRepository.save(new Cart(customerId)));
	}
}
