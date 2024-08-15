package camp.woowak.lab.web.api.coupon;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;

import camp.woowak.lab.common.advice.DomainExceptionHandler;
import camp.woowak.lab.common.exception.HttpStatusException;
import camp.woowak.lab.coupon.exception.DuplicateCouponTitleException;

@DomainExceptionHandler(basePackageClasses = CouponApiController.class)
public class CouponExceptionHandler {

	/**
	 *
	 *  DuplicateCouponTitleException.class 를 처리한다.
	 */
	@ExceptionHandler(value = DuplicateCouponTitleException.class)
	public ProblemDetail handleDuplicateCouponTitleException(DuplicateCouponTitleException e) {
		return getProblemDetail(e, HttpStatus.CONFLICT);
	}

	private ProblemDetail getProblemDetail(HttpStatusException e, HttpStatus status) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, e.errorCode().getMessage());
		problemDetail.setProperty("errorCode", e.errorCode().getErrorCode());
		return problemDetail;
	}
}
