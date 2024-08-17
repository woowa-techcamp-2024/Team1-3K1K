package camp.woowak.lab.web.resolver.store;

public enum StoreFilterBy {
	CATEGORY_NAME("cn"), MIN_PRICE("mp");
	private String value;

	StoreFilterBy(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static StoreFilterBy getFilterBy(String value) {
		for (StoreFilterBy s : StoreFilterBy.values()) {
			if (s.value().equals(value)) {
				return s;
			}
		}
		return null;
	}
}
