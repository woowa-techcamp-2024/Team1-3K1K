package camp.woowak.lab.web.resolver.session;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import camp.woowak.lab.common.exception.UnauthorizedException;
import camp.woowak.lab.web.authentication.AuthenticationErrorCode;
import camp.woowak.lab.web.authentication.LoginVendor;
import camp.woowak.lab.web.authentication.annotation.AuthenticationPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionVendorArgumentResolver extends LoginMemberArgumentResolver {
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(AuthenticationPrincipal.class)
			&& LoginVendor.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
								  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		AuthenticationPrincipal parameterAnnotation = parameter.getParameterAnnotation(AuthenticationPrincipal.class);
		if (parameterAnnotation == null) {
			return null;
		}
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
		HttpSession session = request.getSession(false);
		if (parameterAnnotation.required() &&
			(session == null || session.getAttribute(SessionConst.SESSION_VENDOR_KEY) == null)) {
			throw new UnauthorizedException(AuthenticationErrorCode.UNAUTHORIZED, "Vendor가 세션에 저장되어 있지 않습니다.");
		}
		return session == null ? null : session.getAttribute(SessionConst.SESSION_VENDOR_KEY);
	}
}
