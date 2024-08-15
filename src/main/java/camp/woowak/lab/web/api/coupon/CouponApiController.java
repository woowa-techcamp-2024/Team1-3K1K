package camp.woowak.lab.web.api.coupon;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.coupon.service.IssueCouponService;
import camp.woowak.lab.coupon.service.command.IssueCouponCommand;
import camp.woowak.lab.web.dto.request.coupon.IssueCouponRequest;
import camp.woowak.lab.web.dto.response.coupon.IssueCouponResponse;

@RestController
public class CouponApiController {
	private final IssueCouponService issueCouponService;

	public CouponApiController(IssueCouponService issueCouponService) {
		this.issueCouponService = issueCouponService;
	}

	@PostMapping("/coupons")
	@ResponseStatus(HttpStatus.CREATED)
	public IssueCouponResponse issueCoupon(@RequestBody IssueCouponRequest request) {
		IssueCouponCommand cmd = new IssueCouponCommand(request.title(), request.discountAmount(),
			request.quantity(), request.expiredAt());

		Long couponId = issueCouponService.issueCoupon(cmd);

		return new IssueCouponResponse(couponId);
	}

}
