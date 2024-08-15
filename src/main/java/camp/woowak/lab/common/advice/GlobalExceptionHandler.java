package camp.woowak.lab.common.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import camp.woowak.lab.common.exception.ErrorCode;
import camp.woowak.lab.common.exception.HttpStatusException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ProblemDetail handleAllUncaughtException(Exception e) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
			e.getMessage());
		problemDetail.setProperty("errorCode", "9999");

		log.error("[Unexpected Exception]", e);
		// TODO: Notion Hook 등록

		return problemDetail;
	}

	@ExceptionHandler(HttpStatusException.class)
	public ProblemDetail handleException(HttpStatusException exception) {
		log.info(exception.getMessage(), exception);
		ErrorCode errorCode = exception.errorCode();
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
			HttpStatus.valueOf(errorCode.getStatus()), exception.errorCode().getMessage());
		problemDetail.setProperty("error_code", errorCode.getErrorCode());
		return problemDetail;
	}
}
