package camp.woowak.lab.order.domain;

import java.util.List;

import org.springframework.stereotype.Component;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.order.exception.EmptyCartException;
import camp.woowak.lab.order.exception.MultiStoreOrderException;

@Component
public class SingleStoreOrderValidator implements OrderValidator {
	@Override
	public void check(Customer requester, List<Menu> orderedMenus) {
		if (orderedMenus == null || orderedMenus.isEmpty()) {
			throw new EmptyCartException("최소 하나 이상의 메뉴를 주문해야 합니다.");
		}
		Long storeId = orderedMenus.get(0).getStore().getId();
		for (Menu orderedMenu : orderedMenus) {
			if (!orderedMenu.getStore().getId().equals(storeId)) {
				throw new MultiStoreOrderException("다른 가게의 메뉴를 같이 주문할 수 없습니다.");
			}
		}
	}
}
