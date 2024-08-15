package camp.woowak.lab.order.domain;

import java.util.List;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.order.exception.NotEnoughBalanceException;

public class PayAmountValidator implements OrderValidator {
	@Override
	public void check(Customer requester, List<Menu> orderedMenus) {
		Integer totalPrice = 0;
		for (Menu orderedMenu : orderedMenus) {
			totalPrice += orderedMenu.getPrice();
		}
		if (requester.getPayAccount().getBalance() < totalPrice) {
			throw new NotEnoughBalanceException("구매자 " + requester.getId() + "가 잔고가 부족하여 주문에 실패했습니다.");
		}
	}
}
