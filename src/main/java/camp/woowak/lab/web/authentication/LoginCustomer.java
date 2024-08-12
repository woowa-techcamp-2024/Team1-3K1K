package camp.woowak.lab.web.authentication;

public class LoginCustomer implements LoginMember {
	private final Long id;

	public LoginCustomer(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
