package camp.woowak.lab.web.api.customer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import camp.woowak.lab.common.advice.DomainExceptionHandler;
import camp.woowak.lab.common.exception.BadRequestException;
import camp.woowak.lab.common.exception.HttpStatusException;
import camp.woowak.lab.customer.exception.CustomerAuthenticationException;
import camp.woowak.lab.customer.exception.DuplicateEmailException;
import camp.woowak.lab.customer.exception.InvalidCreationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DomainExceptionHandler
public class CustomerExceptionHandler extends ResponseEntityExceptionHandler {
	/**
	 *
	 *  BadRequestException.class 와 MethodArgumentNotValidException.class 를 처리한다.
	 */
	@ExceptionHandler({InvalidCreationException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handleBadRequestException(BadRequestException e) {
		return getProblemDetail(e, HttpStatus.BAD_REQUEST);
	}

	/**
	 *
	 *  DuplicateEmailException.class 를 처리한다.
	 */
	@ExceptionHandler({DuplicateEmailException.class})
	@ResponseStatus(HttpStatus.CONFLICT)
	public ProblemDetail handleDuplicateEmailException(DuplicateEmailException e) {
		return getProblemDetail(e, HttpStatus.CONFLICT);
	}

	@ExceptionHandler({CustomerAuthenticationException.class})
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ProblemDetail handleCustomerAuthenticationException(CustomerAuthenticationException e) {
		return getProblemDetail(e, HttpStatus.UNAUTHORIZED);
	}

	private ProblemDetail getProblemDetail(HttpStatusException e, HttpStatus status) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, e.errorCode().getMessage());
		problemDetail.setProperty("errorCode", e.errorCode().getErrorCode());
		return problemDetail;
	}
}
