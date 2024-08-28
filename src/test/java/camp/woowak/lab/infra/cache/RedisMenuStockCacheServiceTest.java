package camp.woowak.lab.infra.cache;

import static camp.woowak.lab.infra.cache.redis.RedisCacheConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import camp.woowak.lab.infra.cache.redis.RedisMenuStockCacheService;

@ExtendWith(MockitoExtension.class)
class RedisMenuStockCacheServiceTest {
	@InjectMocks
	private RedisMenuStockCacheService redisMenuStockCacheService;

	@Mock
	private RedissonClient redissonClient;

	@Nested
	@DisplayName("Redis 에 상품 메뉴 재고수를 캐싱하는 기능은")
	class UpdateStock {
		@Test
		@DisplayName("[성공] 재고수가 캐싱되어 있지 않고, 메뉴ID에 락이 걸려있지 않으면, 메뉴 재고를 캐싱한다.")
		void testUpdateStockNotCacheAndNotLocked() throws InterruptedException {
			// given
			Long menuId = 1L;
			Long stock = 10L;

			RLock lockMock = mock(RLock.class);
			RAtomicLong atomicLongMock = mock(RAtomicLong.class);

			when(redissonClient.getAtomicLong(MENU_STOCK_PREFIX + menuId)).thenReturn(atomicLongMock);
			when(atomicLongMock.isExists()).thenReturn(false);
			when(redissonClient.getLock(anyString())).thenReturn(lockMock);
			when(lockMock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);

			// when
			redisMenuStockCacheService.updateStock(menuId, stock);

			// then
			verify(atomicLongMock, times(1)).isExists();
			verify(redissonClient, times(1)).getLock(contains(LOCK_PREFIX));
			verify(lockMock, times(1)).unlock();
		}

		@Test
		@DisplayName("[성공] 재고수가 캐싱되어 있지 않고, 메뉴ID에 락이 걸려있으면, 캐싱된 메뉴 재고 확인 재시도 후 최근에 캐싱된 재고수를 반환한다.")
		void testUpdateStockNotCacheAndLocked() throws InterruptedException {
			// given
			Long menuId = 1L;
			Long initialStock = 10L;
			Long recentlyCachedStock = 5L;  // 락이 해제된 후 설정될 재고

			RLock lockMock = mock(RLock.class);
			RAtomicLong atomicLongMock = mock(RAtomicLong.class);

			when(redissonClient.getAtomicLong(MENU_STOCK_PREFIX + menuId)).thenReturn(atomicLongMock);
			when(redissonClient.getLock(anyString())).thenReturn(lockMock);
			when(atomicLongMock.isExists()).thenReturn(false).thenReturn(true);    // 첫 시도에는 없고, 두 번째 시도에는 있음
			when(lockMock.tryLock(anyLong(), anyLong(), any())).thenReturn(false);  // 첫 시도에는 실패
			when(atomicLongMock.get()).thenReturn(recentlyCachedStock);

			// when
			Long result = redisMenuStockCacheService.updateStock(menuId, initialStock);

			// then
			assertEquals(recentlyCachedStock, result, "반환된 재고 수가 예상과 다릅니다.");
			verify(atomicLongMock, times(2)).isExists();
			verify(redissonClient, times(1)).getLock(contains(LOCK_PREFIX));
			verify(lockMock, times(1)).tryLock(anyLong(), anyLong(), any());
		}

		@Test
		@DisplayName("[성공] 재고수가 캐싱되어 있으면 그 재고수를 반환한다.")
		void testUpdateStockCached() {
			// given
			Long menuId = 1L;
			Long stock = 10L;

			RAtomicLong atomicLongMock = mock(RAtomicLong.class);

			when(redissonClient.getAtomicLong(MENU_STOCK_PREFIX + menuId)).thenReturn(atomicLongMock);
			when(atomicLongMock.isExists()).thenReturn(true);
			when(atomicLongMock.get()).thenReturn(stock);

			// when
			Long result = redisMenuStockCacheService.updateStock(menuId, stock);

			// then
			assertEquals(stock, result, "반환된 재고 수가 예상과 다릅니다.");
			verify(atomicLongMock, times(1)).isExists();
			verify(atomicLongMock, times(1)).get();
		}

		@Test
		@DisplayName("[예외] RedissonClient 가 Lock 생성에 실패하면 받은 재고수를 반환한다.")
		void testUpdateStockFailedToCreateLock() throws InterruptedException {
			// given
			Long menuId = 1L;
			Long stock = 10L;

			RLock lockMock = mock(RLock.class);
			RAtomicLong atomicLongMock = mock(RAtomicLong.class);

			when(redissonClient.getAtomicLong(MENU_STOCK_PREFIX + menuId)).thenReturn(atomicLongMock);
			when(redissonClient.getLock(anyString())).thenReturn(lockMock);
			when(atomicLongMock.isExists()).thenReturn(false);
			when(lockMock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, LOCK_TIME_UNIT)).thenThrow(
				new InterruptedException());

			// when
			Long result = redisMenuStockCacheService.updateStock(menuId, stock);

			// then
			assertEquals(stock, result, "반환된 재고 수가 예상과 다릅니다.");
		}

		@Test
		@DisplayName("[예외] Redis 연결이 중간에 실패하여 getAtomicLong 이 예외를 발생할 때, 받은 재고수를 반환한다.")
		void testUpdateStockFailedToConnectRedis() {
			// given
			Long menuId = 1L;
			Long stock = 10L;

			when(redissonClient.getAtomicLong(MENU_STOCK_PREFIX + menuId)).thenThrow(
				new NullPointerException("Redis 연결 실패"));

			// when
			Long result = redisMenuStockCacheService.updateStock(menuId, stock);

			// then
			assertEquals(stock, result, "반환된 재고 수가 예상과 다릅니다.");
		}
	}
}
