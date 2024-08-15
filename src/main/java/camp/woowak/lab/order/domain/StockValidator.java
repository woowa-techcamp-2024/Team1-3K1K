package camp.woowak.lab.order.domain;

import java.util.List;

import org.springframework.stereotype.Component;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.menu.domain.Menu;

@Component
public class StockValidator implements OrderValidator {
	@Override
	public void check(Customer requester, List<Menu> orderedMenus) {

	}
}
