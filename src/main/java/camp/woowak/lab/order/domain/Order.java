package camp.woowak.lab.order.domain;

import java.util.ArrayList;
import java.util.List;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.store.domain.Store;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ORDERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	private Customer requester;
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;
	@CollectionTable(name = "ORDER_ITEMS", joinColumns = @JoinColumn(name = "order_id"))
	@ElementCollection(fetch = FetchType.EAGER)
	private List<OrderItem> orderItems = new ArrayList<>();

	public Order(Customer requester, Store store, List<CartItem> cartItems,
				 SingleStoreOrderValidator singleStoreOrderValidator,
				 StockRequester stockRequester, PriceChecker priceChecker, WithdrawPointService withdrawPointService) {
		singleStoreOrderValidator.check(store, cartItems);
		stockRequester.request(cartItems);
		List<OrderItem> orderItems = priceChecker.check(cartItems);
		withdrawPointService.withdraw(requester, orderItems);
		this.requester = requester;
		this.store = store;
		this.orderItems = orderItems;
	}

	public Long getId() {
		return id;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}
}
