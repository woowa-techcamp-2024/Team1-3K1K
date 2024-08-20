package camp.woowak.lab.cart.domain.vo;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CartItem implements Serializable {
	private final Long menuId;
	private final Long storeId;
	private final int amount;

	@JsonCreator
	public CartItem(@JsonProperty("menuId") Long menuId, @JsonProperty("storeId") Long storeId,
					@JsonProperty("amount") Integer amount) {
		this.menuId = menuId;
		this.storeId = storeId;
		this.amount = amount;
	}

	public Long getMenuId() {
		return menuId;
	}

	public Long getStoreId() {
		return storeId;
	}

	public int getAmount() {
		return amount;
	}

	public CartItem add(Integer increment) {
		return new CartItem(menuId, storeId, amount + increment);
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
