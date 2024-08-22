package camp.woowak.lab.cart.domain.vo;

import java.util.Objects;

import lombok.Getter;

@Getter
public class CartItem {
	private Long id;
	private final Long menuId;
	private final Long storeId;
	private final int amount;

	public CartItem(Long menuId, Long storeId, Integer amount) {
		this.menuId = menuId;
		this.storeId = storeId;
		this.amount = amount;
	}

	public CartItem(Long id, Long menuId, Long storeId, int amount) {
		this.id = id;
		this.menuId = menuId;
		this.storeId = storeId;
		this.amount = amount;
	}

	public CartItem add(Integer increment) {
		return new CartItem(id, menuId, storeId, amount + increment);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CartItem item))
			return false;
		return Objects.equals(menuId, item.menuId) && Objects.equals(storeId, item.storeId)
			&& Objects.equals(amount, item.amount);
	}

	@Override
	public int hashCode() {
		return Objects.hash(menuId, storeId, amount);
	}
}
