package camp.woowak.lab.menu;

import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.store.domain.Store;

public class TestMenu extends Menu {
	private Long id;

	public TestMenu(Long id, Store store, MenuCategory menuCategory,
					String name, Integer price) {
		super(store, menuCategory, name, price, "imageUrl");
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
