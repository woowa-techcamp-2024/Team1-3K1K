package camp.woowak.lab.web.authentication;

import java.util.UUID;

public class LoginCustomer implements LoginMember {
	private final UUID id;

	public LoginCustomer(UUID id) {
		this.id = id;
	}

	@Override
	public UUID getId() {
		return id;
	}
}
