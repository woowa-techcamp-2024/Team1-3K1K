package camp.woowak.lab.web.dto.response.order;

import java.util.List;

import camp.woowak.lab.order.domain.Order;

public record RetrieveOrderListResponse(List<Order> orders) {
}
