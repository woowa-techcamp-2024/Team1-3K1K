package camp.woowak.lab.web.api.cart;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.cart.service.CartService;
import camp.woowak.lab.cart.service.command.AddCartCommand;
import camp.woowak.lab.cart.service.command.CartTotalPriceCommand;
import camp.woowak.lab.web.authentication.LoginCustomer;
import camp.woowak.lab.web.authentication.annotation.AuthenticationPrincipal;
import camp.woowak.lab.web.dto.request.cart.AddCartRequest;
import camp.woowak.lab.web.dto.response.cart.AddCartResponse;
import camp.woowak.lab.web.dto.response.cart.CartTotalPriceResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/cart")
@Slf4j
public class CartApiController {
	private final CartService cartService;

	public CartApiController(CartService cartService) {
		this.cartService = cartService;
	}

	@PostMapping
	public AddCartResponse addCart(
		@AuthenticationPrincipal LoginCustomer loginCustomer,
		@RequestBody AddCartRequest addCartRequest) {
		AddCartCommand command = new AddCartCommand(loginCustomer.getId().toString(), addCartRequest.menuId());
		cartService.addMenu(command);

		log.info("Customer({}) add Menu({}) in Cart", loginCustomer.getId(), addCartRequest.menuId());
		return new AddCartResponse(true);
	}

	@GetMapping("/price")
	public CartTotalPriceResponse getCartTotalPrice(
		@AuthenticationPrincipal LoginCustomer loginCustomer
	) {
		CartTotalPriceCommand command = new CartTotalPriceCommand(loginCustomer.getId().toString());
		long totalPrice = cartService.getTotalPrice(command);

		log.info("Customer({})'s total price in cart is {}", loginCustomer.getId(), totalPrice);
		return new CartTotalPriceResponse(totalPrice);
	}
}
