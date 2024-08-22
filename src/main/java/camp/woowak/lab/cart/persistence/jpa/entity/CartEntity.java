package camp.woowak.lab.cart.persistence.jpa.entity;

import java.util.List;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true, nullable = false)
	private String customerId;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "cart_id")
	private List<CartItemEntity> cartItems;

	public Cart toDomain() {
		List<CartItem> cartItems = this.cartItems.stream().map(CartItemEntity::toDomain).collect(Collectors.toList());
		return new Cart(id, customerId, cartItems);
	}

	public static CartEntity fromDomain(Cart cart) {
		CartEntity entity = new CartEntity();
		entity.id = cart.getId();
		entity.customerId = cart.getCustomerId();
		entity.cartItems = cart.getCartItems().stream().map(CartItemEntity::fromDomain).collect(Collectors.toList());
		return entity;
	}
}
