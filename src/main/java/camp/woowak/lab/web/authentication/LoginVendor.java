package camp.woowak.lab.web.authentication;

import java.util.UUID;

public class LoginVendor implements LoginMember {
	private final UUID id;

	public LoginVendor(UUID id) {
		this.id = id;
	}

	@Override
	public UUID getId() {
		return id;
	}
}
