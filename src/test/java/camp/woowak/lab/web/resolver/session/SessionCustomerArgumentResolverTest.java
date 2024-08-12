package camp.woowak.lab.web.resolver.session;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import camp.woowak.lab.common.exception.UnauthorizedRequestException;
import camp.woowak.lab.web.authentication.LoginCustomer;
import camp.woowak.lab.web.authentication.LoginVendor;
import camp.woowak.lab.web.authentication.annotation.AuthenticationPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
public class SessionCustomerArgumentResolverTest {
	@InjectMocks
	private SessionCustomerArgumentResolver resolver;
	@Mock
	private MethodParameter methodParameter;
	@Mock
	private NativeWebRequest webRequest;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpSession session;
	@Mock
	private LoginCustomer mockCustomer;

	@Nested
	@DisplayName("supportsParameter")
	class SupportsParameter {
		@Test
		@DisplayName("[True] 파라미터에 @AuthenticationPrincipal이 붙은 경우")
		public void supportsParameter_ReturnsTrue_WhenCorrectAnnotationAndType() {
			when(methodParameter.hasParameterAnnotation(AuthenticationPrincipal.class)).thenReturn(true);
			when(methodParameter.getParameterType()).thenReturn((Class)LoginCustomer.class);

			boolean supports = resolver.supportsParameter(methodParameter);

			assertThat(supports).isTrue();
		}

		@Test
		@DisplayName("[False] 파라미터가 LoginCustomer가 아닌 경우")
		public void supportsParameter_ReturnsTrue_WhenIncorrectType() {
			when(methodParameter.hasParameterAnnotation(AuthenticationPrincipal.class)).thenReturn(true);
			when(methodParameter.getParameterType()).thenReturn((Class)LoginVendor.class);

			boolean supports = resolver.supportsParameter(methodParameter);

			assertThat(supports).isFalse();
		}

		@Test
		@DisplayName("[False] 파라미터에 @AuthenticationPrincipal이 붙지 않은 경우")
		public void supportsParameter_ReturnsFalse_WhenIncorrectAnnotationOrType() {
			when(methodParameter.hasParameterAnnotation(AuthenticationPrincipal.class)).thenReturn(false);

			boolean supports = resolver.supportsParameter(methodParameter);

			assertThat(supports).isFalse();
		}
	}

	@Nested
	@DisplayName("resolveArgument")
	class ResolveArgument {
		@Test
		@DisplayName("[LoginCustomer] 세션에 LoginCustomer가 있는 경우")
		public void resolveArgument_ReturnsCustomer_WhenSessionExists() throws Exception {
			when(webRequest.getNativeRequest()).thenReturn(request);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute(SessionConst.SESSION_CUSTOMER_KEY)).thenReturn(mockCustomer);
			when(methodParameter.getParameterAnnotation(AuthenticationPrincipal.class)).thenReturn(
				new AuthenticationPrincipal() {
					@Override
					public Class<? extends Annotation> annotationType() {
						return null;
					}

					@Override
					public boolean required() {
						return true;
					}
				}
			);

			Object result = resolver.resolveArgument(methodParameter, null, webRequest, null);

			assertThat(result).isEqualTo(mockCustomer);
		}

		@Test
		@DisplayName("[Null] 파라미터에 @AuthenticationPrincipal이 붙지 않은 경우")
		public void resolveArgument_ReturnsNull_WhenSessionDoesNotExist() throws Exception {
			Object result = resolver.resolveArgument(methodParameter, null, webRequest, null);

			assertThat(result).isNull();
		}

		@Test
		@DisplayName("[UnauthorizedRequestException] @AuthenticationPrincipal(required=true)인데 세션이 LoginCustomer가 없는 경우")
		public void resolveArgument_ThrowsException_WhenSessionRequiredButMissing() {
			when(webRequest.getNativeRequest()).thenReturn(request);
			when(methodParameter.getParameterAnnotation(AuthenticationPrincipal.class)).thenReturn(
				new AuthenticationPrincipal() {
					@Override
					public boolean required() {
						return true;
					}

					@Override
					public Class<? extends Annotation> annotationType() {
						return AuthenticationPrincipal.class;
					}
				});

			assertThrows(
				UnauthorizedRequestException.class,
				() -> resolver.resolveArgument(methodParameter, null, webRequest, null));
		}
	}
}
