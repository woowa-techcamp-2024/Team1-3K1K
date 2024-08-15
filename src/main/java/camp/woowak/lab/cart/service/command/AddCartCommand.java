package camp.woowak.lab.cart.service.command;

public record AddCartCommand(
	Long customerId,
	Long menuId
) {
}
