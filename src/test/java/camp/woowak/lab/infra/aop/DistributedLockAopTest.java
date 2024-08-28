package camp.woowak.lab.infra.aop;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

@ExtendWith(MockitoExtension.class)
class DistributedLockAopTest {

	@Mock
	private RedissonClient redissonClient;

	@Mock
	private AopForTransaction aopForTransaction;

	@Mock
	private ProceedingJoinPoint proceedingJoinPoint;

	@Mock
	private MethodSignature methodSignature;

	@Mock
	private RLock rlock;

	@InjectMocks
	private DistributedLockAop distributedLockAop;

	@Nested
	@DisplayName("Redisson 분산락 AOP 의 around 메서드는")
	class AroundTest {

		@Nested
		@DisplayName("tryLock 으로 락 획득에 성공하면")
		class WhenAcquireLock {

			@Test
			@DisplayName("[Success] AOP 의 실제 메서드를 실행하고, 메서드 실행 후 락을 해제한다.")
			void success() throws Throwable {
				// given
				Method method = TestCouponService.class.getDeclaredMethod("issueCouponWithDistributionLock",
					Long.class);
				DistributedLock distributedLockAnnotation = method.getAnnotation(DistributedLock.class);
				Long couponId = 456L;
				Long expectedResult = 456L;

				given(proceedingJoinPoint.getSignature()).willReturn(methodSignature);
				given(methodSignature.getMethod()).willReturn(method);
				given(methodSignature.getParameterNames()).willReturn(new String[] {"couponId"});
				given(proceedingJoinPoint.getArgs()).willReturn(new Object[] {couponId});

				try (MockedStatic<CustomSpringELParser> mockedParser = mockStatic(CustomSpringELParser.class)) {
					mockedParser.when(() -> CustomSpringELParser.getDynamicValue(any(), any(), any()))
						.thenReturn(couponId.toString());

					given(redissonClient.getLock(anyString())).willReturn(rlock);
					given(rlock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).willReturn(true);
					given(aopForTransaction.proceed(proceedingJoinPoint)).willReturn(couponId);
					given(rlock.isHeldByCurrentThread()).willReturn(true);

					// when: 분산락을 적용한 AOP around 메서드를 호출했을 때
					Object result = distributedLockAop.around(proceedingJoinPoint);

					// then: AOP 분산락의 절차에 맞게 검증
					InOrder inOrder = inOrder(redissonClient, rlock, aopForTransaction);
					inOrder.verify(redissonClient).getLock(eq("LOCK:" + couponId));    // 1. 올바른 키로 락 객체를 가져오는지 검증
					inOrder.verify(rlock).tryLock(anyLong(), anyLong(), any());    // 2. 가져온 락 객체로 실제 락 획득을 시도하는지 검증
					inOrder.verify(aopForTransaction)
						.proceed(proceedingJoinPoint);    // 3. 락 획득 후 실제 메서드(joinPoint)를 실행하는지 검증
					inOrder.verify(rlock).unlock();    // 4. 메서드 실행 후 락을 해제하는지 검증
					assertThat(result).isEqualTo(expectedResult);    // 5. AOP가 래핑된 메서드의 실행 결과를 올바르게 반환하는지 검증
				}
			}
		}

		@Nested
		@DisplayName("tryLock 으로 락 획득에 실패하면")
		class WhenFailAcquireLock {

			@Test
			@DisplayName("[Exception] AOP 의 실제 메서드를 실행하지 않고 unLock 도 실행하지 않는다.")
			void test() throws Throwable {
				// given
				Method method = TestCouponService.class.getDeclaredMethod("issueCouponWithDistributionLock",
					Long.class);
				Long couponId = 789L;

				given(proceedingJoinPoint.getSignature()).willReturn(methodSignature);
				given(methodSignature.getMethod()).willReturn(method);
				given(methodSignature.getParameterNames()).willReturn(new String[] {"couponId"});
				given(proceedingJoinPoint.getArgs()).willReturn(new Object[] {couponId});

				try (MockedStatic<CustomSpringELParser> mockedParser = mockStatic(CustomSpringELParser.class)) {
					mockedParser.when(() -> CustomSpringELParser.getDynamicValue(any(), any(), any()))
						.thenReturn(couponId.toString());
					given(redissonClient.getLock(anyString())).willReturn(rlock);
					given(rlock.tryLock(anyLong(), anyLong(), any())).willReturn(false);    // 락 획득을 실패

					// when: 분산락을 적용한 AOP around 메서드를 호출했을 때
					assertThatThrownBy(() -> distributedLockAop.around(proceedingJoinPoint))
						.isInstanceOf(IllegalStateException.class);

					// then: AOP 분산락의 절차에 맞게 검증
					InOrder inOrder = inOrder(redissonClient, rlock, aopForTransaction);
					inOrder.verify(redissonClient).getLock(eq("LOCK:" + couponId));    // 1. 올바른 키로 락 객체를 가져오는지 검증
					inOrder.verify(rlock).tryLock(anyLong(), anyLong(), any());    // 2. 가져온 락 객체로 실제 락 획득을 시도하는지 검증
					inOrder.verify(aopForTransaction, never())
						.proceed(proceedingJoinPoint);    // 3. 락 획득 실패 시 메서드를 실행하지 않는지 검증
					inOrder.verify(rlock, never()).unlock();    // 4. 락 획득 실패 시 락을 해제하지 않는지 검증
				}
			}
		}

		@Nested
		@DisplayName("tryLock 에서 InterruptedException 발생 시,")
		class InterruptedExceptionAtTryLockTest {

			@Test
			@DisplayName("[Exception] AOP 의 실제 메서드를 실행하지 않고 unLock 도 실행하지 않는다.")
			void testInterruptedExceptionDuringTryLock() throws Throwable {
				// given
				Method method = TestCouponService.class.getDeclaredMethod("issueCouponWithDistributionLock",
					Long.class);
				Long couponId = 123L;

				given(proceedingJoinPoint.getSignature()).willReturn(methodSignature);
				given(methodSignature.getMethod()).willReturn(method);
				given(methodSignature.getParameterNames()).willReturn(new String[] {"couponId"});
				given(proceedingJoinPoint.getArgs()).willReturn(new Object[] {couponId});

				try (MockedStatic<CustomSpringELParser> mockedParser = mockStatic(CustomSpringELParser.class)) {
					mockedParser.when(() -> CustomSpringELParser.getDynamicValue(any(), any(), any()))
						.thenReturn(couponId.toString());
					given(redissonClient.getLock(anyString())).willReturn(rlock);
					given(rlock.tryLock(anyLong(), anyLong(), any())).willThrow(
						new InterruptedException("Test interruption"));

					// when & then
					assertThatThrownBy(() -> distributedLockAop.around(proceedingJoinPoint))
						.isInstanceOf(InterruptedException.class)
						.hasMessage("Test interruption");

					verify(rlock, never()).unlock();
					InOrder inOrder = inOrder(redissonClient, rlock, aopForTransaction);
					inOrder.verify(redissonClient).getLock(eq("LOCK:" + couponId));    // 1. 올바른 키로 락 객체를 가져오는지 검증
					inOrder.verify(rlock).tryLock(anyLong(), anyLong(), any());    // 2. 가져온 락 객체로 실제 락 획득을 시도하는지 검증
					inOrder.verify(aopForTransaction, never())
						.proceed(proceedingJoinPoint);    // 3. 락 획득 실패 시 메서드를 실행하지 않는지 검증
					inOrder.verify(rlock, never()).unlock();    // 4. 락 획득 실패 시 락을 해제하지 않는지 검증
				}
			}
		}

		@Nested
		@DisplayName("unLock 에서 IllegalMonitorStateException 발생 시,")
		class IllegalMonitorStateExceptionAtUnLockTest {

			@Test
			@DisplayName("[Exception 락은 이미 unLock 상태이다.")
			void testIllegalMonitorStateExceptionDuringUnlock() throws Throwable {
				// given
				Method method = TestCouponService.class.getDeclaredMethod("issueCouponWithDistributionLock",
					Long.class);
				Long couponId = 456L;

				given(proceedingJoinPoint.getSignature()).willReturn(methodSignature);
				given(methodSignature.getMethod()).willReturn(method);
				given(methodSignature.getParameterNames()).willReturn(new String[] {"couponId"});
				given(proceedingJoinPoint.getArgs()).willReturn(new Object[] {couponId});

				try (MockedStatic<CustomSpringELParser> mockedParser = mockStatic(CustomSpringELParser.class)) {
					mockedParser.when(() -> CustomSpringELParser.getDynamicValue(any(), any(), any()))
						.thenReturn(couponId.toString());
					given(redissonClient.getLock(anyString())).willReturn(rlock);
					given(rlock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
					given(rlock.isHeldByCurrentThread()).willReturn(true);
					doThrow(new IllegalMonitorStateException("Test exception")).when(rlock).unlock();

					// when & then
					assertThatThrownBy(() -> distributedLockAop.around(proceedingJoinPoint))
						.isInstanceOf(IllegalMonitorStateException.class)
						.hasMessage("Test exception");

					verify(rlock).tryLock(anyLong(), anyLong(), any());
					verify(rlock).unlock();
				}
			}

		}

	}
	
	/**
	 * AOP 분산 락 테스트를 위한 테스트용 서비스 클래스
	 */
	static class TestCouponService {

		@DistributedLock(key = "#couponId", leaseTime = 5000L)
		public Long issueCouponWithDistributionLock(Long couponId) {
			return couponId;
		}

		@DistributedLock(key = "#command.couponId()", leaseTime = 5000L)
		public Long issueCouponWithDistributedLock(TestCouponCommand command) {
			return command.couponId();
		}

	}

	record TestCouponCommand(Long couponId) {
	}

}