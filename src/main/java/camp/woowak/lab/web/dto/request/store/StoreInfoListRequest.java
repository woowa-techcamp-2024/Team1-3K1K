package camp.woowak.lab.web.dto.request.store;

import lombok.Getter;

@Getter
public class StoreInfoListRequest {
	public static final int DEFAULT_PAGE_SIZE = 20;
	public static final int DEFAULT_PAGE_NUMBER = 0;

	private final int size;//서버에서 사이즈는 고정하기로
	private final int page;

	public StoreInfoListRequest(int page) {
		this.page = page;
		this.size = DEFAULT_PAGE_SIZE;
	}
}
