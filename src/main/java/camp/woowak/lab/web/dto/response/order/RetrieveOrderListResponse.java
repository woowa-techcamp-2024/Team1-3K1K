package camp.woowak.lab.web.dto.response.order;

import java.util.List;

import camp.woowak.lab.order.service.dto.OrderDTO;

public record RetrieveOrderListResponse(List<OrderDTO> orders) {
}
