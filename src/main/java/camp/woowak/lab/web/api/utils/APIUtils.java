package camp.woowak.lab.web.api.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * API Utils
 * of Method는 status와 data를 이용해 APIResponse객체를 만들 수 있습니다.
 *
 */
public final class APIUtils {
	private APIUtils() {
	}

	public static <T> ResponseEntity<APIResponse<T>> of(HttpStatus status, T data) {
		return new ResponseEntity<>(new APIResponse<>(status, data), status);
	}
}
