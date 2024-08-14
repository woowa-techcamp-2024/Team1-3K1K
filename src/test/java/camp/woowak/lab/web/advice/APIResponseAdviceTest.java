package camp.woowak.lab.web.advice;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;

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

}