package camp.woowak.lab.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import camp.woowak.lab.common.advice.DomainExceptionHandler;
import camp.woowak.lab.common.exception.BadRequestException;

@DomainExceptionHandler
public class CustomerExceptionHandler {

	/**
	 *
	 *  BadRequestException.class 와 MethodArgumentNotValidException.class 를 처리한다.
	 */
	@ExceptionHandler({BadRequestException.class, MethodArgumentNotValidException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handleBadRequestException(BadRequestException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
	}
}
