package camp.woowak.lab.web.api.utils;

import org.springframework.http.HttpStatus;

/**
 * APIResponse를 Jackson의 ObjectMapper와 함께 사용하려면,
 * Generic Type의 {@code data}에는 Getter 메서드가 필요합니다.
 */
public class APIResponse<T> {
	private final T data;
	private final int status;

	public APIResponse(final HttpStatus status, final T data) {
		this.data = data;
		this.status = status.value();
	}

	public T getData() {
		return data;
	}

	public int getStatus() {
		return status;
	}
}
