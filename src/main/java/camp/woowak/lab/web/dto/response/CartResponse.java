package camp.woowak.lab.web.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class CartResponse {
	private Long storeId;
	private String storeName;
	private Integer minOrderPrice;
	private List<CartItemInfo> menus = new ArrayList<>();

	public CartResponse() {
	}

	public CartResponse(Long storId, String storeName, Integer minOrderPrice, List<CartItemInfo> menus) {
		this.storeId = storId;
		this.storeName = storeName;
		this.minOrderPrice = minOrderPrice;
		this.menus = menus;
	}

	@Getter
	public static class CartItemInfo {
		private Long menuId;
		private String menuName;
		private Long menuPrice;
		private Long amount;
		private Long leftAmount;

		public CartItemInfo(Long menuId, String menuName, Long menuPrice, Long amount, Long leftAmount) {
			this.menuId = menuId;
			this.menuName = menuName;
			this.menuPrice = menuPrice;
			this.amount = amount;
			this.leftAmount = leftAmount;
		}
	}
}
