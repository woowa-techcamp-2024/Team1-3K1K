package camp.woowak.lab.web.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@Configuration
public class AuthenticationConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new NoOpPasswordEncoder();
	}
}
