package camp.woowak.lab.web.config;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import camp.woowak.lab.web.resolver.session.SessionCustomerArgumentResolver;
import camp.woowak.lab.web.resolver.session.SessionVendorArgumentResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

	private final SessionVendorArgumentResolver sessionVendorArgumentResolver;
	private final SessionCustomerArgumentResolver sessionCustomerArgumentResolver;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(sessionVendorArgumentResolver);
		resolvers.add(sessionCustomerArgumentResolver);
	}
}
