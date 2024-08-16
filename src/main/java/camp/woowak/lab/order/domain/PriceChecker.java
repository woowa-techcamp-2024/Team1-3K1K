package camp.woowak.lab.order.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.order.exception.NotFoundMenuException;

@Component
public class PriceChecker {
	private final MenuRepository menuRepository;

	public PriceChecker(MenuRepository menuRepository) {
		this.menuRepository = menuRepository;
	}

	public List<OrderItem> check(List<CartItem> cartItems) {
		List<Menu> menus = menuRepository.findAllById(cartItems.stream().map(CartItem::getMenuId).toList());
		if (menus.size() != cartItems.size()) {
			throw new NotFoundMenuException("등록되지 않은 메뉴를 주문했습니다.");
		}
		List<OrderItem> orderItems = new ArrayList<>();
		for (Menu menu : menus) {
			for (CartItem cartItem : cartItems) {
				if (cartItem.getMenuId().equals(menu.getId())) {
					OrderItem orderItem = new OrderItem(menu.getId(), menu.getPrice(), cartItem.getAmount());
					orderItems.add(orderItem);
				}
			}
		}
		return orderItems;
	}
}
