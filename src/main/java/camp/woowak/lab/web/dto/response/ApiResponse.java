package camp.woowak.lab.web.dto.response;

import camp.woowak.lab.web.error.ErrorCode;

public class ApiResponse<T> {
	private String code;
	private String message;
	private T data;

	private ApiResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}

	private ApiResponse(String code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>("OK", "success", data);
	}

	public static <T> ApiResponse<T> error(ErrorCode errorCode) {
		return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage());
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public T getData() {
		return data;
	}
}
