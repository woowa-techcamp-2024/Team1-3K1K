package camp.woowak.lab.web.api.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.order.service.OrderCreationService;
import camp.woowak.lab.order.service.RetrieveOrderListService;
import camp.woowak.lab.order.service.command.OrderCreationCommand;
import camp.woowak.lab.order.service.command.RetrieveOrderListCommand;
import camp.woowak.lab.web.authentication.LoginCustomer;
import camp.woowak.lab.web.authentication.LoginVendor;
import camp.woowak.lab.web.authentication.annotation.AuthenticationPrincipal;
import camp.woowak.lab.web.dto.response.order.OrderCreationResponse;
import camp.woowak.lab.web.dto.response.order.RetrieveOrderListResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OrderApiController {
	private final OrderCreationService orderCreationService;
	private final RetrieveOrderListService retrieveOrderListService;

	public OrderApiController(OrderCreationService orderCreationService,
							  RetrieveOrderListService retrieveOrderListService) {
		this.orderCreationService = orderCreationService;
		this.retrieveOrderListService = retrieveOrderListService;
	}

	@GetMapping("/orders")
	public RetrieveOrderListResponse retrieveOrderList(@AuthenticationPrincipal LoginVendor loginVendor) {
		RetrieveOrderListCommand command = new RetrieveOrderListCommand(loginVendor.getId());
		return new RetrieveOrderListResponse(retrieveOrderListService.retrieveOrderListOfVendorStores(command));
	}

	@GetMapping("/orders/stores/{storeId}")
	public RetrieveOrderListResponse retrieveOrderListByStore(@AuthenticationPrincipal LoginVendor loginVendor,
															  @PathVariable(name = "storeId") Long storeId) {
		RetrieveOrderListCommand command = new RetrieveOrderListCommand(storeId, loginVendor.getId());
		return new RetrieveOrderListResponse(retrieveOrderListService.retrieveOrderListOfStore(command));
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
