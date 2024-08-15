package camp.woowak.lab.coupon.service.command;

import java.time.LocalDateTime;

public record IssueCouponCommand(String title, int discountAmount, int quantity, LocalDateTime expiredAt) {
}
