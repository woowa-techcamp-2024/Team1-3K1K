package camp.woowak.lab.web.dto.response.order;

import java.util.List;
import java.util.UUID;

public record OrderResponse(
	Long id,
	RequesterInfo requester,
	StoreInfo store,
	List<OrderItemInfo> orderItems
) {

	public record RequesterInfo(
		UUID id,
		String name,
		String email,
		String phone
	) {
	}

	public record StoreInfo(
		Long id,
		String name,
		String ownerName,
		String address,
		String phoneNumber
	) {
	}

	public record OrderItemInfo(
		Long menuId,
		long price,
		int quantity,
		long totalPrice
	) {
	}
}
