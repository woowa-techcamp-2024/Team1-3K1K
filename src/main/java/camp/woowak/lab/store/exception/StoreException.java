package camp.woowak.lab.store.exception;

import lombok.Getter;

@Getter
public class StoreException extends RuntimeException {

	private final ErrorCode errorCode;

	public StoreException(final ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	@Getter
	public enum ErrorCode {

		INVALID_NAME_RANGE("가게 이름은 2글자 ~ 10글자 이어야합니다."),

		INVALID_ADDRESS("가게 주소는 송파구만 가능합니다."),

		INVALID_MIN_ORDER_PRICE("최소 주문 금액은 5,000원 이상이어야 합니다."),
		INVALID_UNIT_OF_MIN_ORDER_PRICE("최소 주문 금액은 1,000원 단위이어야 합니다."),

		INVALID_TIME_UNIT("가게 시작 시간은 분 단위까지 가능합니다"),
		INVALID_TIME("가게 시작 시간은 종료 시간보다 이전이어야 합니다"),

		INVALID_STORE_CATEGORY("존재하지 않는 가게 카테고리입니다.");

		private final String message;

		ErrorCode(String message) {
			this.message = message;
		}

	}

}
