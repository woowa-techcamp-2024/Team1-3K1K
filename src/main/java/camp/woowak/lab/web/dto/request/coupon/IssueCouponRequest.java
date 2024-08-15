package camp.woowak.lab.web.dto.request.coupon;

import java.time.LocalDateTime;

public record IssueCouponRequest(String title, int discountAmount, int quantity, LocalDateTime expiredAt) {
}
