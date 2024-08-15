package camp.woowak.lab.order.domain;

import java.util.List;

import camp.woowak.lab.menu.domain.Menu;

public interface OrderValidator {
	void check(List<Menu> orderedMenus);
}
