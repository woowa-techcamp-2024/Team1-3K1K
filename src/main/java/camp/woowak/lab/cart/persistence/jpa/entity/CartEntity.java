package camp.woowak.lab.cart.persistence.jpa.entity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.domain.vo.CartItem;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true, nullable = false)
	private UUID customerId;
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItemEntity> cartItems;
	@Version
	private long version;

	public Cart toDomain() {
		List<CartItem> cartItems = this.cartItems.stream().map(CartItemEntity::toDomain).collect(Collectors.toList());
		return new Cart(id, customerId.toString(), cartItems);
	}

	public static CartEntity fromDomain(Cart cart) {
		CartEntity entity = new CartEntity();
		entity.id = cart.getId();
		entity.customerId = UUID.fromString(cart.getCustomerId());
		entity.cartItems = cart.getCartItems()
			.stream()
			.map((cartItem) -> CartItemEntity.fromDomain(entity, cartItem))
			.collect(Collectors.toList());
		return entity;
	}
}
