package camp.woowak.lab.order.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class OrderItem {
	private Long menuId;
	private long price;
	private int quantity;
	private long totalPrice;

	protected OrderItem() {
	}

	public OrderItem(Long menuId, long price, int quantity) {
		this.menuId = menuId;
		this.price = price;
		this.quantity = quantity;
		this.totalPrice = price * quantity;
	}
}
