package camp.woowak.lab.web.api.payaccount;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import camp.woowak.lab.common.advice.DomainExceptionHandler;
import camp.woowak.lab.common.exception.BadRequestException;
import camp.woowak.lab.common.exception.ErrorCode;
import camp.woowak.lab.common.exception.NotFoundException;
import camp.woowak.lab.payaccount.exception.NotFoundAccountException;
import lombok.extern.slf4j.Slf4j;

//TODO : exception구체화 및 error code 정의
@DomainExceptionHandler(basePackageClasses = PayAccountApiController.class)
@Slf4j
public class PayAccountApiControllerAdvice {
	@ExceptionHandler(value = {BadRequestException.class})
	public ResponseEntity<ProblemDetail> badRequestException(BadRequestException e) {
		log.warn("Bad Request", e);
		ErrorCode errorCode = e.errorCode();
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorCode.getMessage());
		problemDetail.setProperty("errorCode", errorCode.getErrorCode());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
	}

	@ExceptionHandler(value = {NotFoundAccountException.class})
	public ResponseEntity<ProblemDetail> notFoundAccountException(NotFoundException e) {
		log.warn("Not Found Request", e);
		ErrorCode errorCode = e.errorCode();
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, errorCode.getMessage());
		problemDetail.setProperty("errorCode", errorCode.getErrorCode());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
	}
}
