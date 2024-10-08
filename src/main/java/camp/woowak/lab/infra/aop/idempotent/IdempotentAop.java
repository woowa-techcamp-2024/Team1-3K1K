package camp.woowak.lab.infra.aop.idempotent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.Duration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import camp.woowak.lab.infra.aop.idempotent.exception.IdempotencyKeyNotExistsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotentAop {
	private static final String REDIS_IDEMPOTENT_KEY = "IDEMPOTENT_KEY: ";
	private final RedisTemplate<String, Object> redisTemplate;

	@Around("@annotation(camp.woowak.lab.infra.aop.idempotent.Idempotent)")
	public Object idempotentOperation(ProceedingJoinPoint joinPoint) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String idempotencyKey = request.getHeader("Idempotency-Key");
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		Idempotent idempotent = method.getAnnotation(Idempotent.class);

		if (idempotencyKey == null || idempotencyKey.isEmpty()) {
			throw new IdempotencyKeyNotExistsException("Idempotency-Key is required");
		}

		if (Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_IDEMPOTENT_KEY + idempotencyKey))) {
			if (idempotent.throwError()) {
				Class<? extends RuntimeException> throwable = idempotent.throwable();
				String exceptionMessage = idempotent.exceptionMessage();
				Constructor<? extends RuntimeException> constructor = throwable.getConstructor(String.class);
				RuntimeException exceptions = constructor.newInstance(exceptionMessage);

				throw exceptions;
			}
			return redisTemplate.opsForValue().get(REDIS_IDEMPOTENT_KEY + idempotencyKey);
		}

		Object proceed = joinPoint.proceed();
		redisTemplate.opsForValue()
			.setIfAbsent(REDIS_IDEMPOTENT_KEY + idempotencyKey, proceed, Duration.ofSeconds(60L));
		return proceed;
	}
}
