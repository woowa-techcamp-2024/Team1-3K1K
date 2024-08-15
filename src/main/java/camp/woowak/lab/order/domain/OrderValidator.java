package camp.woowak.lab.order.domain;

import java.util.List;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.menu.domain.Menu;

public interface OrderValidator {
	void check(Customer requester, List<Menu> orderedMenus);
}
