package camp.woowak.lab.menu;

import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.store.domain.Store;

public class TestMenuCategory extends MenuCategory {
	private Long id;

	public TestMenuCategory(Long id, Store store, String name) {
		super(store, name);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
