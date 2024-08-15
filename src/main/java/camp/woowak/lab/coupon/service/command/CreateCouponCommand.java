package camp.woowak.lab.coupon.service.command;

import java.time.LocalDateTime;

public record CreateCouponCommand(String title, int discountAmount, int quantity, LocalDateTime expiredAt) {
}
