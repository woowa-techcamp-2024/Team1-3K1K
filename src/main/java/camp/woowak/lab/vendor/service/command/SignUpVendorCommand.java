package camp.woowak.lab.vendor.service.command;

public record SignUpVendorCommand(
	String name,
	String email,
	String password,
	String phone
) {
}
