package camp.woowak.lab.web.dto.request;

public record SignUpVendorRequest(
	String name,
	String email,
	String password,
	String phone
) {
}
