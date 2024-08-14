package camp.woowak.lab.cart.domain;

import java.util.LinkedList;
import java.util.List;

import camp.woowak.lab.menu.domain.Menu;

public class Cart {
	private Long customerId;
	private List<Menu> menuList;

	/**
	 * 생성될 때 무조건 cart가 비어있도록 구현
	 *
	 * @param customerId 장바구니 소유주의 ID값입니다.
	 */
	public Cart(Long customerId) {
		this.customerId = customerId;
		this.menuList = new LinkedList<>();
	}
}
