package camp.woowak.lab.web.api.cart;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import camp.woowak.lab.common.advice.DomainExceptionHandler;
import camp.woowak.lab.common.exception.BadRequestException;
import camp.woowak.lab.common.exception.ErrorCode;
import camp.woowak.lab.common.exception.HttpStatusException;
import lombok.extern.slf4j.Slf4j;

@DomainExceptionHandler
@Slf4j
public class CartApiControllerAdvice {
	@ExceptionHandler(value = {BadRequestException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail badRequestException(BadRequestException e) {
		log.warn("Bad Request", e);
		return getProblemDetail(e, HttpStatus.BAD_REQUEST);
	}

	private ProblemDetail getProblemDetail(HttpStatusException exception, HttpStatus httpStatus) {
		ErrorCode errorCode = exception.errorCode();
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, errorCode.getMessage());
		problemDetail.setProperty("errorCode", errorCode.getErrorCode());

		return problemDetail;
	}
}
