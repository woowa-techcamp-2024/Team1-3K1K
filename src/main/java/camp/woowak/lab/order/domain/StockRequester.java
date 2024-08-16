package camp.woowak.lab.order.domain;

import java.util.List;

import org.springframework.stereotype.Component;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.repository.MenuRepository;

@Component
public class StockRequester {
	private final MenuRepository menuRepository;

	public StockRequester(MenuRepository menuRepository) {
		this.menuRepository = menuRepository;
	}

	public void request(List<CartItem> cartItems) {
		List<Menu> allById = menuRepository.findAllByIdForUpdate(cartItems.stream().map(CartItem::getMenuId).toList());
		for (Menu menu : allById) {
			for (CartItem cartItem : cartItems) {
				if (cartItem.getMenuId().equals(menu.getId())) {
					menu.decrementStockCount(cartItem.getAmount());
				}
			}
		}
	}
}
