package camp.woowak.lab.web.api.payaccount;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import camp.woowak.lab.payaccount.exception.InsufficientBalanceException;
import camp.woowak.lab.payaccount.exception.InvalidTransactionAmountException;
import camp.woowak.lab.payaccount.exception.NotFoundAccountException;
import lombok.extern.slf4j.Slf4j;

//TODO : exception구체화 및 error code 정의
@RestControllerAdvice(basePackageClasses = PayAccountApiController.class)
@Slf4j
public class PayAccountApiControllerAdvice {
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<?> bindingException(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError)error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		log.warn("Bad Request with parameter binding : {}", errors);
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(value = {InsufficientBalanceException.class, InvalidTransactionAmountException.class})
	public ResponseEntity<?> badRequestException(Exception e) {
		log.warn("Bad Request", e);
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(value = {NotFoundAccountException.class})
	public ResponseEntity<?> notFoundAccountException(Exception e) {
		log.warn("Not Found Request", e);
		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler(value = {Exception.class})
	public ResponseEntity<?> internalServerException(Exception e) {
		log.error("Internal Server Error", e);
		return ResponseEntity.internalServerError().build();
	}
}
