package camp.woowak.lab.web.api.order;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;

import camp.woowak.lab.common.advice.DomainExceptionHandler;
import camp.woowak.lab.common.exception.HttpStatusException;
import camp.woowak.lab.store.exception.NotEqualsOwnerException;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DomainExceptionHandler(basePackageClasses = OrderApiController.class)
public class OrderExceptionHandler {
	@ExceptionHandler(value = NotFoundStoreException.class)
	public ProblemDetail handleNotFoundStoreException(NotFoundStoreException e) {
		log.error("Not Found", e);
		return getProblemDetail(HttpStatus.NOT_FOUND, e);
	}

	@ExceptionHandler(value = NotEqualsOwnerException.class)
	public ProblemDetail handleNotEqualsOwnerException(NotEqualsOwnerException e) {
		log.error("Not Equals Owner", e);
		return getProblemDetail(HttpStatus.UNAUTHORIZED, e);
	}

	private ProblemDetail getProblemDetail(HttpStatus status, HttpStatusException e) {
		return ProblemDetail.forStatusAndDetail(status, e.errorCode().getMessage());
	}
}
