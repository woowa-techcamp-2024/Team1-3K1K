package camp.woowak.lab.web.authentication;

public class LoginVendor implements LoginMember {
	private final Long id;

	public LoginVendor(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
