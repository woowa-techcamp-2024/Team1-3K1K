package camp.woowak.lab.web.api.coupon;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.coupon.service.CreateCouponService;
import camp.woowak.lab.coupon.service.IssueCouponService;
import camp.woowak.lab.coupon.service.command.CreateCouponCommand;
import camp.woowak.lab.coupon.service.command.IssueCouponCommand;
import camp.woowak.lab.web.authentication.LoginCustomer;
import camp.woowak.lab.web.authentication.annotation.AuthenticationPrincipal;
import camp.woowak.lab.web.dto.request.coupon.CreateCouponRequest;
import camp.woowak.lab.web.dto.response.coupon.CreateCouponResponse;
import camp.woowak.lab.web.dto.response.coupon.IssueCouponResponse;
import jakarta.validation.Valid;

@RestController
public class CouponApiController {
	private final CreateCouponService createCouponService;
	private final IssueCouponService issueCouponService;

	public CouponApiController(CreateCouponService createCouponService, IssueCouponService issueCouponService) {
		this.createCouponService = createCouponService;
		this.issueCouponService = issueCouponService;
	}

	@PostMapping("/coupons")
	@ResponseStatus(HttpStatus.CREATED)
	public CreateCouponResponse createCoupon(@Valid @RequestBody CreateCouponRequest request) {
		CreateCouponCommand cmd = new CreateCouponCommand(request.title(), request.discountAmount(),
			request.quantity(), request.expiredAt());

		Long couponId = createCouponService.createCoupon(cmd);

		return new CreateCouponResponse(couponId);
	}

	@PostMapping("/coupons/{couponId}/issue")
	@ResponseStatus(HttpStatus.CREATED)
	public IssueCouponResponse issueCoupon(@AuthenticationPrincipal LoginCustomer loginCustomer,
										   @PathVariable Long couponId) {
		IssueCouponCommand cmd = new IssueCouponCommand(loginCustomer.getId(), couponId);

		issueCouponService.issueCoupon(cmd);

		return new IssueCouponResponse();
	}
}
