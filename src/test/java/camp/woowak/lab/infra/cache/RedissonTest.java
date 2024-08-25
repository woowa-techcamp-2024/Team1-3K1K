package camp.woowak.lab.infra.cache;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;
import org.redisson.api.TransactionOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import camp.woowak.lab.infra.cache.redis.RedisCacheConstants;

@SpringBootTest
class RedissonTest {

	@Autowired
	RedissonClient redissonClient;

	@Test
	void testAtomicOp() {
		RAtomicLong atomicLong = redissonClient.getAtomicLong(RedisCacheConstants.MENU_STOCK_PREFIX + 2);
		atomicLong.set(100);
		int THREAD_COUNT = 10;
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
		CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

		for (int i = 0; i < THREAD_COUNT; i++) {
			executor.execute(() -> {
				RTransaction tx = redissonClient.createTransaction(TransactionOptions.defaults());
				atomicLong.getAndAdd(-10);
				tx.commit();
				latch.countDown();
			});
		}

		try {
			latch.await();
			executor.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals(0, atomicLong.get());
	}

	@Test
	@DisplayName("Redisson 의 getAndSet API는")
	void getAndSetTest() throws Throwable {
		int TOTAL_THREAD_COUNT = 10;
		Long originCachedStock = 10L;
		Long expectedStockCount = 0L;

		RAtomicLong atomicLong = redissonClient.getAtomicLong(RedisCacheConstants.MENU_STOCK_PREFIX + 1);
		atomicLong.set(originCachedStock);

		ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_THREAD_COUNT);
		CountDownLatch countDownLatch = new CountDownLatch(TOTAL_THREAD_COUNT);

		for (int personCount = 0; personCount < TOTAL_THREAD_COUNT; personCount++) {
			executorService.execute(() -> {
					atomicLong.getAndAdd(-1L);
					countDownLatch.countDown();
				}
			);
		}

		countDownLatch.await();
		executorService.shutdown();
		assertThat(atomicLong.get()).isEqualTo(expectedStockCount);
	}
}
