package camp.woowak.lab.web.dao.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.web.dto.response.order.OrderResponse;

public interface OrderDao {
	Page<Order> findAll(OrderQuery query, Pageable pageable);
}
