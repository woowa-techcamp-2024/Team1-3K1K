package camp.woowak.lab.order.domain;

import java.util.List;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.menu.domain.Menu;

public class CompositeOrderValidator implements OrderValidator {
	private final List<OrderValidator> orderValidators;

	public CompositeOrderValidator(List<OrderValidator> orderValidators) {
		this.orderValidators = orderValidators;
	}

	@Override
	public void check(Customer requester, List<Menu> orderedMenus) {
		for (OrderValidator orderValidator : orderValidators) {
			orderValidator.check(requester, orderedMenus);
		}
	}
}
