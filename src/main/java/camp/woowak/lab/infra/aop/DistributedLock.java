package camp.woowak.lab.infra.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
	/**
	 * @return 분산락의 키 이름
	 */
	String key();

	/**
	 * @return 분산락 단위 시간
	 */
	TimeUnit timeUnit() default TimeUnit.SECONDS;

	/**
	 * @return 분산락 대기 시간
	 */
	long waitTime() default 5L;

	/**
	 * @return 분산락을 점유한 스레드가 정해진 시간이 지나면 락을 해제
	 */
	long leaseTime() default 5L;

	/**
	 * @return 분산락을 획득하지 못한 스레드들이 던져야하는 exception
	 */
	Class<? extends RuntimeException> throwable();

	/**
	 * @return 분산락을 획득하지 못했을 때의 메세지
	 */
	String exceptionMessage();
}
