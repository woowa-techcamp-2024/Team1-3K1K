package camp.woowak.lab.web.api.coupon;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;

import camp.woowak.lab.common.advice.DomainExceptionHandler;
import camp.woowak.lab.common.exception.HttpStatusException;
import camp.woowak.lab.coupon.exception.DuplicateCouponTitleException;
import camp.woowak.lab.coupon.exception.ExpiredCouponException;
import camp.woowak.lab.coupon.exception.InvalidICreationIssuanceException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DomainExceptionHandler(basePackageClasses = CouponApiController.class)
public class CouponExceptionHandler {

	/**
	 *
	 *  DuplicateCouponTitleException.class 를 처리한다.
	 */
	@ExceptionHandler(value = DuplicateCouponTitleException.class)
	public ProblemDetail handleDuplicateCouponTitleException(DuplicateCouponTitleException e) {
		log.warn("Conflict", e.getMessage());
		return getProblemDetail(e, HttpStatus.CONFLICT);
	}

	/**
	 *
	 * InvalidICreationIssuanceException.class 를 처리한다.
	 */
	@ExceptionHandler(value = InvalidICreationIssuanceException.class)
	public ProblemDetail handleInvalidICreationIssuanceException(InvalidICreationIssuanceException e) {
		log.warn("Bad Request", e.getMessage());
		return getProblemDetail(e, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = ExpiredCouponException.class)
	public ProblemDetail handleExpiredCouponException(ExpiredCouponException e) {
		log.warn("Conflict", e.getMessage());
		return getProblemDetail(e, HttpStatus.CONFLICT);
	}

	private ProblemDetail getProblemDetail(HttpStatusException e, HttpStatus status) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, e.errorCode().getMessage());
		problemDetail.setProperty("errorCode", e.errorCode().getErrorCode());
		return problemDetail;
	}
}
