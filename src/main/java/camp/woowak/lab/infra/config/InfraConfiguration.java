package camp.woowak.lab.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import camp.woowak.lab.infra.date.CurrentDateTime;
import camp.woowak.lab.infra.date.DateTimeProvider;

@Configuration
public class InfraConfiguration {

	@Bean
	public DateTimeProvider dateTimeProvider() {
		return new CurrentDateTime();
	}

}
