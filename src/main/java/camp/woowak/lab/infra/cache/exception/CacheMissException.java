package camp.woowak.lab.infra.cache.exception;

public class CacheMissException extends RuntimeException {
	public CacheMissException(String message) {
		super(message);
	}
}
