package camp.woowak.lab.cart.persistence.jpa.entity;

import camp.woowak.lab.cart.domain.vo.CartItem;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class CartItemEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long menuId;
	private Long storeId;
	private int amount;

	@ManyToOne
	private CartEntity cart;

	public CartItem toDomain() {
		return new CartItem(id, menuId, storeId, amount);
	}

	public static CartItemEntity fromDomain(CartItem cartItem) {
		CartItemEntity cartItemEntity = new CartItemEntity();
		cartItemEntity.id = cartItem.getId();
		cartItemEntity.menuId = cartItem.getMenuId();
		cartItemEntity.storeId = cartItem.getStoreId();
		cartItemEntity.amount = cartItem.getAmount();
		return cartItemEntity;
	}
}
