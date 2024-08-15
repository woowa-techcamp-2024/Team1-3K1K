package camp.woowak.lab.web.api.store;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

import camp.woowak.lab.common.exception.ErrorCode;
import camp.woowak.lab.common.exception.HttpStatusException;
import camp.woowak.lab.store.exception.StoreException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class StoreExceptionHandler {

	@ExceptionHandler(StoreException.class)
	public ResponseEntity<?> handleException(StoreException exception) {
		log.warn(exception.getMessage(), exception);
		return new ResponseEntity<>("fail", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpStatusCodeException.class)
	public ProblemDetail handleException(HttpStatusException exception) {
		log.info(exception.getMessage(), exception);
		ErrorCode errorCode = exception.errorCode();
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
			HttpStatus.valueOf(errorCode.getStatus()), exception.errorCode().getMessage());
		problemDetail.setProperty("error_code", errorCode.getErrorCode());
		return problemDetail;
	}
}
