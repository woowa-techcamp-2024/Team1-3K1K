package camp.woowak.lab.order.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class OrderItem {
	private Long menuId;
	private int price;
	private int quantity;
	private int totalPrice;

	protected OrderItem() {
	}

	public OrderItem(Long menuId, int price, int quantity) {
		this.menuId = menuId;
		this.price = price;
		this.quantity = quantity;
		this.totalPrice = price * quantity;
	}
}
