package camp.woowak.lab.vendor.service.command;

public record SignInVendorCommand(
	String email,
	String password
) {
}
