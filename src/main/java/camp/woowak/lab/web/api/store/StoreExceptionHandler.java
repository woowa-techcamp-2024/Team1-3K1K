package camp.woowak.lab.web.api.store;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import camp.woowak.lab.common.advice.DomainExceptionHandler;
import camp.woowak.lab.common.exception.ErrorCode;
import camp.woowak.lab.common.exception.HttpStatusException;
import camp.woowak.lab.menu.exception.InvalidMenuCategoryCreationException;
import camp.woowak.lab.menu.exception.InvalidMenuCreationException;
import camp.woowak.lab.menu.exception.NotFoundMenuCategoryException;
import camp.woowak.lab.store.exception.InvalidStoreCreationException;
import camp.woowak.lab.store.exception.NotEqualsOwnerException;
import camp.woowak.lab.store.exception.NotFoundStoreCategoryException;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DomainExceptionHandler(basePackageClasses = StoreApiController.class)
public class StoreExceptionHandler {

	@ExceptionHandler(InvalidStoreCreationException.class)
	public ResponseEntity<ProblemDetail> handleException(InvalidStoreCreationException exception) {
		log.warn("Bad Request", exception);
		HttpStatus badRequest = HttpStatus.BAD_REQUEST;
		ProblemDetail problemDetail = getProblemDetail(exception, badRequest);

		return ResponseEntity.status(badRequest).body(problemDetail);
	}

	@ExceptionHandler(NotEqualsOwnerException.class)
	public ResponseEntity<ProblemDetail> handleException(NotEqualsOwnerException exception) {
		log.warn("Bad Request", exception);
		HttpStatus badRequest = HttpStatus.BAD_REQUEST;
		ProblemDetail problemDetail = getProblemDetail(exception, badRequest);

		return ResponseEntity.status(badRequest).body(problemDetail);
	}

	@ExceptionHandler(NotFoundStoreCategoryException.class)
	public ResponseEntity<ProblemDetail> handleException(NotFoundStoreCategoryException exception) {
		log.warn("Not Found", exception);
		HttpStatus badRequest = HttpStatus.BAD_REQUEST;
		ProblemDetail problemDetail = getProblemDetail(exception, badRequest);

		return ResponseEntity.status(badRequest).body(problemDetail);
	}

	@ExceptionHandler(NotFoundStoreException.class)
	public ResponseEntity<ProblemDetail> handleException(NotFoundStoreException exception) {
		log.warn("Not Found", exception);
		HttpStatus badRequest = HttpStatus.NOT_FOUND;
		ProblemDetail problemDetail = getProblemDetail(exception, badRequest);

		return ResponseEntity.status(badRequest).body(problemDetail);
	}

	@ExceptionHandler(InvalidMenuCategoryCreationException.class)
	public ResponseEntity<ProblemDetail> handleException(InvalidMenuCategoryCreationException exception) {
		log.warn("Bad Request", exception);
		HttpStatus badRequest = HttpStatus.BAD_REQUEST;
		ProblemDetail problemDetail = getProblemDetail(exception, badRequest);

		return ResponseEntity.status(badRequest).body(problemDetail);
	}

	@ExceptionHandler(InvalidMenuCreationException.class)
	public ResponseEntity<ProblemDetail> handleException(InvalidMenuCreationException exception) {
		log.warn("Bad Request", exception);
		HttpStatus badRequest = HttpStatus.BAD_REQUEST;
		ProblemDetail problemDetail = getProblemDetail(exception, badRequest);

		return ResponseEntity.status(badRequest).body(problemDetail);
	}

	@ExceptionHandler(NotFoundMenuCategoryException.class)
	public ResponseEntity<ProblemDetail> handleException(NotFoundMenuCategoryException exception) {
		log.warn("Not Found", exception);
		HttpStatus badRequest = HttpStatus.NOT_FOUND;
		ProblemDetail problemDetail = getProblemDetail(exception, badRequest);

		return ResponseEntity.status(badRequest).body(problemDetail);
	}

	private ProblemDetail getProblemDetail(HttpStatusException exception, HttpStatus httpStatus) {
		ErrorCode errorCode = exception.errorCode();
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, errorCode.getMessage());
		problemDetail.setProperty("errorCode", errorCode.getErrorCode());

		return problemDetail;
	}

}
