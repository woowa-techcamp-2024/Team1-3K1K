package camp.woowak.lab.cart.persistence.redis.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import camp.woowak.lab.cart.domain.Cart;
import lombok.Getter;

@RedisHash("cart")
@Getter
public class RedisCartEntity implements Serializable {
	@Id
	private String customerId;

	private List<RedisCartItemEntity> cartItems;

	@JsonCreator
	private RedisCartEntity(@JsonProperty("customerId") String customerId,
							@JsonProperty("cartItems") List<RedisCartItemEntity> cartItems) {
		this.customerId = customerId;
		this.cartItems = cartItems == null ? new ArrayList<>() : cartItems;
	}

	public static RedisCartEntity fromDomain(Cart cart) {
		List<RedisCartItemEntity> list = cart.getCartItems().stream()
			.map(RedisCartItemEntity::fromDomain)
			.collect(Collectors.toList());
		return new RedisCartEntity(cart.getCustomerId(), list);
	}

	public Cart toDomain() {
		return new Cart(customerId, cartItems.stream()
			.map(RedisCartItemEntity::toDomain)
			.collect(Collectors.toList()));
	}
}
