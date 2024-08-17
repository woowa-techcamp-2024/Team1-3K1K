package camp.woowak.lab.order.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
		Set<Long> cartItemMenuIds = extractMenuIds(cartItems);
		List<Menu> menus = menuRepository.findAllById(cartItemMenuIds);
		Map<Long, Menu> menuMap = listToMap(menus);
		Set<Long> missingMenuIds = new HashSet<>();
		for (Long menuId : cartItemMenuIds) {
			if (!menuMap.containsKey(menuId)) {
				missingMenuIds.add(menuId);
			}
		}
		if (!missingMenuIds.isEmpty()) {
			String missingIdsString = formatMissingIds(missingMenuIds);
			throw new NotFoundMenuException("등록되지 않은 메뉴(id=" + missingIdsString + ")를 주문했습니다.");
		}
		List<OrderItem> orderItems = new ArrayList<>();
		for (CartItem cartItem : cartItems) {
			Menu menu = menuMap.get(cartItem.getMenuId());
			orderItems.add(new OrderItem(menu.getId(), menu.getPrice(), cartItem.getAmount()));
		}
		return orderItems;
	}

	private String formatMissingIds(Set<Long> missingMenuIds) {
		return missingMenuIds.stream()
			.map(String::valueOf)
			.collect(Collectors.joining(", "));
	}

	private Map<Long, Menu> listToMap(List<Menu> menus) {
		return menus.stream()
			.collect(Collectors.toMap(Menu::getId, Function.identity()));
	}

	private Set<Long> extractMenuIds(List<CartItem> cartItems) {
		return cartItems.stream()
			.map(CartItem::getMenuId)
			.collect(Collectors.toSet());
	}
}
