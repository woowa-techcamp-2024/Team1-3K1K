package camp.woowak.lab.customer.service.command;

public record SignInCustomerCommand(
	String email,
	String password
) {
}
