package camp.woowak.lab.web.dto.request.store;

import lombok.Getter;

@Getter
public class StoreInfoListRequest {
	private final int size;
	private final int page;

	public StoreInfoListRequest(int size, int page) {
		this.size = size;
		this.page = page;
	}
}
