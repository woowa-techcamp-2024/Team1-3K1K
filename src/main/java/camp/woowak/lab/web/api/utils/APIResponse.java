package camp.woowak.lab.web.api.utils;

import org.springframework.http.HttpStatus;

public class APIResponse<T> {
	private final T data;
	private final int status;

	APIResponse(final HttpStatus status, final T data) {
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
