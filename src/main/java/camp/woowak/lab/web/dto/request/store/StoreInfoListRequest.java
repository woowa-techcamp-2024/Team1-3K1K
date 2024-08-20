package camp.woowak.lab.web.dto.request.store;

import static camp.woowak.lab.web.dto.request.store.StoreInfoListRequestConst.*;

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

	public StoreInfoListRequest(int page, String sortBy) {
		this(page, sortBy, DEFAULT_ORDER, null, null);
	}

	public StoreInfoListRequest(int page, String sortBy, int order) {
		this(page, sortBy, order, null, null);
	}

	public StoreInfoListRequest(int page, String sortBy, int order, String filterBy, String filterValue) {
		this.page = page;
		this.size = DEFAULT_PAGE_SIZE;
		this.sortBy = StoreSortBy.getStoreSortBy(sortBy);
		this.order = order;
		this.filterBy = StoreFilterBy.getFilterBy(filterBy);
		this.filterValue = filterValue;
	}
}
