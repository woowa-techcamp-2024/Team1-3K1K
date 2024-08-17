package camp.woowak.lab.web.resolver.store;

public enum StoreSortBy {
	MIN_PRICE("minPrice"), ORDER_COUNT("orderCount");

	private final String value;

	StoreSortBy(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static StoreSortBy getStoreSortBy(String v) {
		for (StoreSortBy s : StoreSortBy.values()) {
			if (s.value.equals(v)) {
				return s;
			}
		}
		return null;
	}
}
