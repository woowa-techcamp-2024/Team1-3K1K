package camp.woowak.lab.web.api.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.order.service.RetrieveOrderListService;
import camp.woowak.lab.order.service.command.RetrieveOrderListCommand;
import camp.woowak.lab.web.authentication.LoginVendor;
import camp.woowak.lab.web.authentication.annotation.AuthenticationPrincipal;
import camp.woowak.lab.web.dto.response.order.RetrieveOrderListResponse;

@RestController
public class OrderApiController {
	private final RetrieveOrderListService retrieveOrderListService;

	public OrderApiController(RetrieveOrderListService retrieveOrderListService) {
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
}
