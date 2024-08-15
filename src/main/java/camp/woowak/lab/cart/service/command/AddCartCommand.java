package camp.woowak.lab.cart.service.command;

public record AddCartCommand(
	String customerId,
	Long menuId
) {
}
