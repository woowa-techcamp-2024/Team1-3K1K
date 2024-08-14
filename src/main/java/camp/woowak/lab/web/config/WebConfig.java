package camp.woowak.lab.web.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import camp.woowak.lab.web.resolver.session.SessionCustomerArgumentResolver;
import camp.woowak.lab.web.resolver.session.SessionVendorArgumentResolver;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	private final SessionCustomerArgumentResolver sessionCustomerArgumentResolver;
	private final SessionVendorArgumentResolver sessionVendorArgumentResolver;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.addAll(List.of(sessionCustomerArgumentResolver, sessionVendorArgumentResolver));
	}
}
