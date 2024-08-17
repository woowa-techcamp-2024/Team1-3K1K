package camp.woowak.lab.web.dto.response.customer;

import java.util.List;

import camp.woowak.lab.customer.service.dto.CustomerDTO;

public record RetrieveCustomerResponse(List<CustomerDTO> customers) {
}
