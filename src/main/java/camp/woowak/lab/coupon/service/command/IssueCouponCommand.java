package camp.woowak.lab.coupon.service.command;

import java.util.UUID;

public record IssueCouponCommand(UUID customerId, Long couponId) {
}
