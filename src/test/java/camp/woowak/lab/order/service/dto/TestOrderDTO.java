package camp.woowak.lab.order.service.dto;

import java.util.List;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.store.domain.Store;

/**
 * order 검증 과정이 복잡해지면서 테스트가 복잡해짐
 * 그로 인해서 쉽게 테스트할 수 있도록 TestOrderDTO를 만들어서 테스트를 진행
 */
public class TestOrderDTO extends OrderDTO {
	public TestOrderDTO(Long id, Customer requester, Store store, List<OrderItem> orderItems) {
		super(id, new RequesterDTO(requester), new StoreDTO(store),
			orderItems.stream().map(OrderItemDTO::new).toList());
	}
}
