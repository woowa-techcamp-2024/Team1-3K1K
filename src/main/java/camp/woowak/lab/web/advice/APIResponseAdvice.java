package camp.woowak.lab.web.advice;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import camp.woowak.lab.web.api.utils.APIResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@RestControllerAdvice
@Slf4j
public class APIResponseAdvice implements ResponseBodyAdvice<Object> {
	private final WebEndpointProperties webEndpointProperties;

	public APIResponseAdvice(WebEndpointProperties webEndpointProperties) {
		this.webEndpointProperties = webEndpointProperties;
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return converterType.isAssignableFrom(MappingJackson2HttpMessageConverter.class)
			&& !returnType.getParameterType().equals(ResponseEntity.class)
			&& !returnType.getParameterType().equals(ProblemDetail.class);
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
								  Class<? extends HttpMessageConverter<?>> selectedConverterType,
								  ServerHttpRequest request, ServerHttpResponse response
	) {
		if (request.getURI().getPath().startsWith(webEndpointProperties.getBasePath())) {
			return body;
		}
		HttpStatus status = getHttpStatus(returnType, (ServletServerHttpResponse)response);
		if (body == null) {
			return new APIResponse<>(status, "No content");
		}

		if (body instanceof APIResponse) {
			return body;
		}

		return new APIResponse<>(status, body);
	}

	private HttpStatus getHttpStatus(MethodParameter returnType, ServletServerHttpResponse response) {
		HttpStatus status;
		if (returnType.hasMethodAnnotation(ResponseStatus.class)) {
			ResponseStatus responseStatus = returnType.getMethodAnnotation(ResponseStatus.class);
			if (responseStatus != null) {
				return responseStatus.value();
			}
		}
		status = HttpStatus.valueOf(response.getServletResponse().getStatus());
		return status;
	}
}
