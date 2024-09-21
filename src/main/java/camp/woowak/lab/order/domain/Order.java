package camp.woowak.lab.order.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.vendor.domain.Vendor;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ORDERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

	private LocalDateTime createdAt;

	public Order(Customer requester, Store store, List<OrderItem> orderItems, LocalDateTime createdAt) {
		this.requester = requester;
		this.store = store;
		this.orderItems = orderItems;
		this.createdAt = createdAt;
	}

	public Customer getRequester() {
		return requester;
	}

	public Store getStore() {
		return store;
	}

	public List<OrderItem> getOrderItems() {
		return Collections.unmodifiableList(orderItems);
	}

	public Vendor getVendor() {
		return store.getOwner();
	}

	public Long calculateTotalPrice() {
		Long totalPrice = 0L;
		for (OrderItem orderItem : orderItems) {
			totalPrice += orderItem.getTotalPrice();
		}
		return totalPrice;
	}

}
