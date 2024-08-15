package camp.woowak.lab.web.api.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * API Utils
 * of Method는 status와 data를 이용해 APIResponse객체를 만들 수 있습니다.
 *
 */
@Deprecated(since = "APIResponseAdvice 에서 APIResponse 를 반환하는 구조여서, 현재는 ResponseEntity 를 사용하지 않습니다.")
public final class APIUtils {
	private APIUtils() {
	}

	@Deprecated
	public static <T> ResponseEntity<APIResponse<T>> of(HttpStatus status, T data) {
		return new ResponseEntity<>(new APIResponse<>(status, data), status);
	}
}
