package camp.woowak.lab.web.resolver.session;

import org.springframework.core.MethodParameter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import camp.woowak.lab.web.authentication.LoginMember;
import camp.woowak.lab.web.authentication.annotation.AuthenticationPrincipal;

public abstract class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(AuthenticationPrincipal.class)
			&& LoginMember.class.isAssignableFrom(parameter.getParameterType());
	}
}
