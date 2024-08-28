package camp.woowak.lab.infra.cache.exception;

public class RedisLockAcquisitionException extends RuntimeException {
	public RedisLockAcquisitionException(String message, Throwable cause) {
		super(message, cause);
	}
}
