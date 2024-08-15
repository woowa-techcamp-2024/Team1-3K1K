package camp.woowak.lab.web.api.coupon;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.coupon.service.CreateCouponService;
import camp.woowak.lab.coupon.service.command.CreateCouponCommand;
import camp.woowak.lab.web.dto.request.coupon.CreateCouponRequest;
import camp.woowak.lab.web.dto.response.coupon.CreateCouponResponse;
import jakarta.validation.Valid;

@RestController
public class CouponApiController {
	private final CreateCouponService createCouponService;

	public CouponApiController(CreateCouponService createCouponService) {
		this.createCouponService = createCouponService;
	}

	@PostMapping("/coupons")
	@ResponseStatus(HttpStatus.CREATED)
	public CreateCouponResponse createCoupon(@Valid @RequestBody CreateCouponRequest request) {
		CreateCouponCommand cmd = new CreateCouponCommand(request.title(), request.discountAmount(),
			request.quantity(), request.expiredAt());

		Long couponId = createCouponService.createCoupon(cmd);

		return new CreateCouponResponse(couponId);
	}

}
