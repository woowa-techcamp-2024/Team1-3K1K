package camp.woowak.lab.web.dto.request.store;

import static camp.woowak.lab.web.dto.request.store.StoreInfoListRequestConst.*;

import camp.woowak.lab.web.resolver.store.StoreFilterBy;
import camp.woowak.lab.web.resolver.store.StoreSortBy;
import lombok.Getter;

@Getter
public class StoreInfoListRequest {
	private final int page;
	private final int size;
	private final StoreSortBy sortBy;
	private final int order;
	private final StoreFilterBy filterBy;
	private final String filterValue;

	public StoreInfoListRequest() {
		this(DEFAULT_PAGE_NUMBER, null, DEFAULT_ORDER, null, null);
	}

	public StoreInfoListRequest(int page) {
		this(page, null);
	}

	public StoreInfoListRequest(int page, StoreSortBy sortBy) {
		this(page, sortBy, DEFAULT_ORDER, null, null);
	}

	public StoreInfoListRequest(int page, StoreSortBy sortBy, int order) {
		this(page, sortBy, order, null, null);
	}

	public StoreInfoListRequest(int page, StoreSortBy sortBy, int order, StoreFilterBy filterBy, String filterValue) {
		this.page = page;
		this.size = DEFAULT_PAGE_SIZE;
		this.sortBy = sortBy;
		this.order = order;
		this.filterBy = filterBy;
		this.filterValue = filterValue;
	}
}
