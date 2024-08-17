package camp.woowak.lab.web.api.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.order.service.OrderCreationService;
import camp.woowak.lab.order.service.command.OrderCreationCommand;
import camp.woowak.lab.web.authentication.LoginCustomer;
import camp.woowak.lab.web.authentication.annotation.AuthenticationPrincipal;
import camp.woowak.lab.web.dto.response.order.OrderCreationResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OrderApiController {
	private final OrderCreationService orderCreationService;

	public OrderApiController(OrderCreationService orderCreationService) {
		this.orderCreationService = orderCreationService;
	}

	@PostMapping("/orders")
	@ResponseStatus(HttpStatus.CREATED)
	public OrderCreationResponse order(@AuthenticationPrincipal LoginCustomer loginCustomer) {
		OrderCreationCommand command = new OrderCreationCommand(loginCustomer.getId());
		Long createdId = orderCreationService.create(command);
		log.info("Created order for customer {} with id {}", loginCustomer.getId(), createdId);
		return new OrderCreationResponse(createdId);
	}
}
