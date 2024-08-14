package camp.woowak.lab.web.api.store;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
