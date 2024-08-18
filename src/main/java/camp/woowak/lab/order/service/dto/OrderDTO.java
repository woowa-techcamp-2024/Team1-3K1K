package camp.woowak.lab.order.service.dto;

import java.util.List;
import java.util.UUID;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.store.domain.Store;
import lombok.Getter;

@Getter
public class OrderDTO {
	private final Long id;
	private final RequesterDTO requester;
	private final StoreDTO store;
	private final List<OrderItemDTO> orderItems;

	public OrderDTO(Long id, RequesterDTO requester, StoreDTO store, List<OrderItemDTO> orderItems) {
		this.id = id;
		this.requester = requester;
		this.store = store;
		this.orderItems = orderItems;
	}

	public OrderDTO(Order order) {
		this.id = order.getId();
		this.requester = new RequesterDTO(order.getRequester());
		this.store = new StoreDTO(order.getStore());
		this.orderItems = order.getOrderItems().stream().map(OrderItemDTO::new).toList();
	}

	@Getter
	public static class RequesterDTO {
		private final UUID id;
		private final String name;
		private final String email;
		private final String phone;

		public RequesterDTO(Customer customer) {
			this.id = customer.getId();
			this.name = customer.getName();
			this.email = customer.getEmail();
			this.phone = customer.getPhone();
		}
	}

	@Getter
	public static class OrderItemDTO {
		private final Long menuId;
		private final long price;
		private final int quantity;
		private final long totalPrice;

		public OrderItemDTO(OrderItem orderItem) {
			this.menuId = orderItem.getMenuId();
			this.price = orderItem.getPrice();
			this.quantity = orderItem.getQuantity();
			this.totalPrice = orderItem.getTotalPrice();
		}
	}

	@Getter
	public static class StoreDTO {
		private final Long id;
		private final String name;
		private final String ownerName;
		private final String address;
		private final String phoneNumber;

		public StoreDTO(Store store) {
			this.id = store.getId();
			this.name = store.getName();
			this.ownerName = store.getOwner().getName();
			this.address = store.getStoreAddress().getDistrict();
			this.phoneNumber = store.getPhoneNumber();
		}
	}
}
