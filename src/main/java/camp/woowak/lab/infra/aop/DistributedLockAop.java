package camp.woowak.lab.infra.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {

	private static final String REDISSON_LOCK_PREFIX = "LOCK:";
	private final RedissonClient redissonClient;
	private final AopForTransaction aopForTransaction;

	@Around("@annotation(camp.woowak.lab.infra.aop.DistributedLock)")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Method method = signature.getMethod();
		DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

		String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(),
			joinPoint.getArgs(),
			distributedLock.key());
		RLock rLock = redissonClient.getLock(key);

		try {
			boolean locked = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(),
				distributedLock.timeUnit());
			if (!locked) {
				log.warn("Failed to acquire lock for method {} with key {}", method.getName(), key);
				throw new IllegalStateException("Unable to acquire lock");
			}

			log.info("Acquired lock for method {} with key {}", method.getName(), key);
			return aopForTransaction.proceed(joinPoint);
		} finally {
			if (rLock.isHeldByCurrentThread()) {
				rLock.unlock();
				log.info("Released lock for method {} with key {}", method.getName(), key);
			}
		}
	}
}
