package camp.woowak.lab.order.service.dto;

import java.util.List;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.store.domain.Store;
import lombok.Getter;

@Getter
public class OrderDTO {
	private final Long id;
	private RequesterDTO requester;
	private StoreDTO store;
	private final List<OrderItemDTO> orderItems;

	public OrderDTO(Long id, List<OrderItem> orderItems) {
		this.id = id;
		this.orderItems = orderItems.stream().map(OrderItemDTO::new).toList();
	}

	public OrderDTO(Order order) {
		this.id = order.getId();
		this.requester = new RequesterDTO(order.getRequester());
		this.store = new StoreDTO(order.getStore());
		this.orderItems = order.getOrderItems().stream().map(OrderItemDTO::new).toList();
	}

	@Getter
	public static class RequesterDTO {
		private final String name;
		private final String email;
		private final String phone;

		public RequesterDTO(Customer customer) {
			this.name = customer.getName();
			this.email = customer.getEmail();
			this.phone = customer.getPhone();
		}
	}

	@Getter
	public static class OrderItemDTO {
		private final Long menuId;
		private final int price;
		private final int quantity;
		private final int totalPrice;

		public OrderItemDTO(OrderItem orderItem) {
			this.menuId = orderItem.getMenuId();
			this.price = orderItem.getPrice();
			this.quantity = orderItem.getQuantity();
			this.totalPrice = orderItem.getTotalPrice();
		}
	}

	@Getter
	public static class StoreDTO {
		private final String name;
		private final String ownerName;
		private final String address;
		private final String phoneNumber;

		public StoreDTO(Store store) {
			this.name = store.getName();
			this.ownerName = store.getOwner().getName();
			this.address = store.getStoreAddress().getDistrict();
			this.phoneNumber = store.getPhoneNumber();
		}
	}
}
