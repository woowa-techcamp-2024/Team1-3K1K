package camp.woowak.lab.web.dto.response.store;

import java.util.List;

import camp.woowak.lab.store.domain.Store;
import lombok.Getter;

@Getter
public class StoreInfoResponse {
	private List<InfoResponse> stores;

	private StoreInfoResponse(List<InfoResponse> stores) {
		this.stores = stores;
	}

	public static StoreInfoResponse of(List<Store> stores) {
		List<InfoResponse> storeInfos = stores.stream()
			.map(InfoResponse::of)
			.toList();

		return new StoreInfoResponse(storeInfos);
	}

	@Getter
	public static class InfoResponse {
		private final Long storeId;
		private final boolean open;
		private final String name;
		private final String category;
		private final int minOrderPrice;

		private InfoResponse(Long storeId, boolean open, String name, String category, int minOrderPrice) {
			this.storeId = storeId;
			this.open = open;
			this.name = name;
			this.category = category;
			this.minOrderPrice = minOrderPrice;
		}

		private static InfoResponse of(Store store) {
			return new InfoResponse(store.getId(), store.isOpen(), store.getName(), store.getStoreCategory().getName(),
				store.getMinOrderPrice());
		}
	}
}
