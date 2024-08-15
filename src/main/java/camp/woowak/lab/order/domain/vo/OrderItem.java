package camp.woowak.lab.order.domain.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public class OrderItem {
	private Long menuId;
	private int quantity;
}
