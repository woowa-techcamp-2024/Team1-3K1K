package camp.woowak.lab.web.dto.request.customer;

/**
 * 이메일 비밀번호 조건을 알 수 없도록 모든 요청을 받을 수 있도록 구현
 */
public record SignInCustomerRequest(
	String email,
	String password
) {
}
