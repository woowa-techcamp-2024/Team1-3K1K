package camp.woowak.lab.web.api.menu;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;

import camp.woowak.lab.common.advice.DomainExceptionHandler;
import camp.woowak.lab.common.exception.HttpStatusException;
import camp.woowak.lab.menu.exception.InvalidMenuStockUpdateException;
import camp.woowak.lab.menu.exception.NotEqualsOwnerException;
import camp.woowak.lab.menu.exception.NotUpdatableTimeException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DomainExceptionHandler(basePackageClasses = MenuApiController.class)
public class MenuExceptionHandler {

	@ExceptionHandler(value = NotUpdatableTimeException.class)
	public ProblemDetail handleInvalidMenuStockUpdateException(NotUpdatableTimeException e) {
		log.warn("Conflict", e);
		return getProblemDetail(HttpStatus.CONFLICT, e);
	}

	@ExceptionHandler(value = NotEqualsOwnerException.class)
	public ProblemDetail handleNotEqualsOwnerException(NotEqualsOwnerException e) {
		log.warn("Bad Request", e);
		return getProblemDetail(HttpStatus.BAD_REQUEST, e);
	}

	@ExceptionHandler(value = InvalidMenuStockUpdateException.class)
	public ProblemDetail handleInvalidMenuStockUpdateException(InvalidMenuStockUpdateException e) {
		log.warn("Conflict", e);
		return getProblemDetail(HttpStatus.CONFLICT, e);
	}

	private ProblemDetail getProblemDetail(HttpStatus status, HttpStatusException e) {
		return ProblemDetail.forStatusAndDetail(status, e.errorCode().getMessage());
	}

}
