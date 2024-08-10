package camp.woowak.lab.web.authentication;

public interface PasswordEncoder {
	String encode(String password);

	boolean matches(String password, String encodedPassword);
}