package camp.woowak.lab.cart.persistence.redis.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import camp.woowak.lab.cart.domain.vo.CartItem;
import lombok.Getter;

@Getter
public class RedisCartItemEntity implements Serializable {
	private Long menuId;
	private Long storeId;
	private int amount;

	@JsonCreator
	private RedisCartItemEntity(@JsonProperty("menuId") Long menuId,
								@JsonProperty("storeId") Long storeId,
								@JsonProperty("amount") int amount) {
		this.menuId = menuId;
		this.storeId = storeId;
		this.amount = amount;
	}

	protected static RedisCartItemEntity fromDomain(CartItem item) {
		return new RedisCartItemEntity(item.getMenuId(), item.getStoreId(), item.getAmount());
	}

	protected CartItem toDomain() {
		return new CartItem(menuId, storeId, amount);
	}
}
