package camp.woowak.lab.web.advice;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ResponseStatus;

import camp.woowak.lab.web.api.utils.APIResponse;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class APIResponseAdviceTest {

	@InjectMocks
	private APIResponseAdvice apiResponseAdvice;

	@Mock
	private MethodParameter methodParameter;

	@Mock
	private ServerHttpRequest request;

	@Mock
	private ServletServerHttpResponse response;

	@Mock
	private HttpServletResponse servletResponse;

	@Mock
	private WebEndpointProperties webEndpointProperties;

	@Nested
	@DisplayName("supports 메서드는")
	class Supports {

		@Test
		@DisplayName("MappingJackson2HttpMessageConverter 타입이고 ResponseEntity나 ProblemDetail이 아닐 때 true를 반환해야 한다")
		void supports_ShouldReturnTrue_WhenConverterTypeIsAssignable() {
			// Given
			Class<? extends HttpMessageConverter<?>> converterType = MappingJackson2HttpMessageConverter.class;
			given(methodParameter.getParameterType()).willReturn((Class)Object.class);

			// When
			boolean result = apiResponseAdvice.supports(methodParameter, converterType);

			// Then
			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("반환 타입이 ResponseEntity일 때 false를 반환해야 한다")
		void supports_ShouldReturnFalse_WhenReturnTypeIsResponseEntity() {
			// Given
			Class<? extends HttpMessageConverter<?>> converterType = MappingJackson2HttpMessageConverter.class;
			given(methodParameter.getParameterType()).willReturn((Class)ResponseEntity.class);

			// When
			boolean result = apiResponseAdvice.supports(methodParameter, converterType);

			// Then
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("반환 타입이 ProblemDetail일 때 false를 반환해야 한다")
		void supports_ShouldReturnFalse_WhenReturnTypeIsProblemDetails() {
			// Given
			Class<? extends HttpMessageConverter<?>> converterType = MappingJackson2HttpMessageConverter.class;
			given(methodParameter.getParameterType()).willReturn((Class)ProblemDetail.class);

			// When
			boolean result = apiResponseAdvice.supports(methodParameter, converterType);

			// Then
			assertThat(result).isFalse();
		}

	}

	@Nested
	@DisplayName("beforeBodyWrite 메서드는")
	class BeforeBodyWrite {
		@Test
		@DisplayName("body가 null일 때 'No content' 메시지를 포함한 APIResponse를 반환해야 한다")
		void beforeBodyWrite_ShouldReturnAPIResponse_WhenBodyIsNull() throws URISyntaxException {
			// Given
			given(webEndpointProperties.getBasePath()).willReturn("/actuator");
			given(request.getURI()).willReturn(new URI("/api"));
			given(servletResponse.getStatus()).willReturn(HttpStatus.OK.value());

			// When
			when(response.getServletResponse()).thenReturn(servletResponse);
			Object result = apiResponseAdvice.beforeBodyWrite(null, methodParameter, MediaType.APPLICATION_JSON,
				MappingJackson2HttpMessageConverter.class, request, response);

			// Then
			assertThat(result).isInstanceOf(APIResponse.class);
			APIResponse<?> apiResponse = (APIResponse<?>)result;
			assertThat(apiResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
			assertThat(apiResponse.getData()).isEqualTo("No content");
		}

		@Test
		@DisplayName("body가 APIResponse가 아닐 때 해당 body를 포함한 새로운 APIResponse를 반환해야 한다")
		void beforeBodyWrite_ShouldReturnAPIResponse_WhenBodyIsNotAPIResponse() throws URISyntaxException {
			// Given
			String body = "Test Body";
			given(webEndpointProperties.getBasePath()).willReturn("/actuator");
			given(request.getURI()).willReturn(new URI("/api"));
			given(servletResponse.getStatus()).willReturn(HttpStatus.OK.value());

			// When
			when(response.getServletResponse()).thenReturn(servletResponse);
			Object result = apiResponseAdvice.beforeBodyWrite(body, methodParameter, MediaType.APPLICATION_JSON,
				MappingJackson2HttpMessageConverter.class, request, response);

			// Then
			assertThat(result).isInstanceOf(APIResponse.class);
			APIResponse<?> apiResponse = (APIResponse<?>)result;
			assertThat(apiResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
			assertThat(apiResponse.getData()).isEqualTo(body);
		}

		@Test
		@DisplayName("beforeBodyWrite 메서드는 ResponseStatus 어노테이션이 있을 때 해당 상태를 사용해야 한다")
		void beforeBodyWrite_ShouldUseResponseStatusAnnotation_WhenPresent() throws URISyntaxException {
			// Given
			String body = "Test Body";
			ResponseStatus responseStatus = mock(ResponseStatus.class);
			given(webEndpointProperties.getBasePath()).willReturn("/actuator");
			given(request.getURI()).willReturn(new URI("/api"));
			given(methodParameter.hasMethodAnnotation(ResponseStatus.class)).willReturn(true);
			given(methodParameter.getMethodAnnotation(ResponseStatus.class)).willReturn(responseStatus);
			given(responseStatus.value()).willReturn(HttpStatus.CREATED);

			// When
			Object result = apiResponseAdvice.beforeBodyWrite(body, methodParameter, MediaType.APPLICATION_JSON,
				MappingJackson2HttpMessageConverter.class, request, response);

			// Then
			assertThat(result).isInstanceOf(APIResponse.class);
			APIResponse<?> apiResponse = (APIResponse<?>)result;
			assertThat(apiResponse.getStatus()).isEqualTo(HttpStatus.CREATED.value());
			assertThat(apiResponse.getData()).isEqualTo(body);
		}

		@Test
		@DisplayName("beforeBodyWrite 메서드는 ResponseStatus 어노테이션이 없을 때 응답의 상태 코드를 사용해야 한다")
		void beforeBodyWrite_ShouldUseResponseStatus_WhenNoAnnotationPresent() throws URISyntaxException {
			// Given
			String body = "Test Body";
			given(webEndpointProperties.getBasePath()).willReturn("/actuator");
			given(request.getURI()).willReturn(new URI("/api"));
			given(methodParameter.hasMethodAnnotation(ResponseStatus.class)).willReturn(false);
			given(servletResponse.getStatus()).willReturn(HttpStatus.OK.value());

			// When
			when(response.getServletResponse()).thenReturn(servletResponse);
			Object result = apiResponseAdvice.beforeBodyWrite(body, methodParameter, MediaType.APPLICATION_JSON,
				MappingJackson2HttpMessageConverter.class, request, response);

			// Then
			assertThat(result).isInstanceOf(APIResponse.class);
			APIResponse<?> apiResponse = (APIResponse<?>)result;
			assertThat(apiResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
			assertThat(apiResponse.getData()).isEqualTo(body);
		}
	}

}
