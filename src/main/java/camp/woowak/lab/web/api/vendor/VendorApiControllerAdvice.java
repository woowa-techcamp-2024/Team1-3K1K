package camp.woowak.lab.web.api.vendor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import camp.woowak.lab.common.advice.DomainExceptionHandler;
import camp.woowak.lab.vendor.exception.DuplicateEmailException;
import camp.woowak.lab.vendor.exception.InvalidVendorCreationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DomainExceptionHandler(basePackageClasses = VendorApiController.class)
public class VendorApiControllerAdvice {
	@ExceptionHandler(InvalidVendorCreationException.class)
	public ResponseEntity<ProblemDetail> handleInvalidVendorCreationException(InvalidVendorCreationException ex) {
		return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage())).build();
	}

	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ProblemDetail> handleDuplicateEmailException(DuplicateEmailException ex) {
		return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage())).build();
	}
}
