package camp.woowak.lab.web.dto.request.coupon;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCouponRequest(
	@NotNull(message = "할인 쿠폰 제목은 null 이 될 수 없습니다.")
	@NotBlank(message = "할인 쿠폰 제목은 공백이 될 수 없습니다.")
	@Length(min = 1, max = 100, message = "할인 쿠폰 제목은 1자 이상 100자 이하여야 합니다.")
	String title,
	@NotNull(message = "할인 금액은 null 이 될 수 없습니다.")
	@Min(value = 0, message = "할인 금액은 0 이상이어야 합니다.")
	int discountAmount,
	@NotNull(message = "할인 쿠폰 수량은 null 이 될 수 없습니다.")
	@Min(value = 0, message = "할인 쿠폰 수량은 0 이상이어야 합니다.")
	int quantity,
	@NotNull(message = "만료일은 null 이 될 수 없습니다.")
	@FutureOrPresent(message = "만료일은 현재 시각 이후여야 합니다.")
	LocalDateTime expiredAt) {
}
