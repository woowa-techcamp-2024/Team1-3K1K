package camp.woowak.lab.web.dto.response.customer;

import java.util.List;

import camp.woowak.lab.customer.domain.Customer;

public record RetrieveCustomerResponse(List<Customer> customers) {
}
