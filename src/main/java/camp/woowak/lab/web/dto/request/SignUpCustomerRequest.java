package camp.woowak.lab.web.dto.request;

public record SignUpCustomerRequest(
	String name,
	String email,
	String password,
	String phone
) {
}
