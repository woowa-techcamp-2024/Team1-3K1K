package camp.woowak.lab.web.authentication;

import java.util.Objects;

public class NoOpPasswordEncoder implements PasswordEncoder {
	@Override
	public String encode(String password) {
		return password;
	}

	@Override
	public boolean matches(String password, String encodedPassword) {
		return Objects.equals(password, encodedPassword);
	}
}