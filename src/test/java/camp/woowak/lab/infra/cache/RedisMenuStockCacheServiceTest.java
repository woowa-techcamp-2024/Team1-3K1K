package camp.woowak.lab.infra.cache;

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
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;
import org.redisson.api.TransactionOptions;
import org.redisson.transaction.TransactionException;

import camp.woowak.lab.infra.cache.redis.RedisCacheConstants;
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

			RTransaction transactionMock = mock(RTransaction.class);
			RBucket bucketMock = mock(RBucket.class);
			RLock lockMock = mock(RLock.class);

			when(redissonClient.createTransaction(any(TransactionOptions.class))).thenReturn(transactionMock);
			when(transactionMock.getBucket(anyString())).thenReturn(bucketMock);
			when(bucketMock.isExists()).thenReturn(false);
			when(redissonClient.getLock(anyString())).thenReturn(lockMock);
			when(lockMock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
			when(lockMock.isHeldByCurrentThread()).thenReturn(true);

			// when
			redisMenuStockCacheService.updateStock(menuId, stock);

			// then
			verify(redissonClient, times(1)).createTransaction(any(TransactionOptions.class));
			verify(transactionMock, times(1)).getBucket(contains(RedisCacheConstants.MENU_STOCK_PREFIX));
			verify(bucketMock, times(1)).isExists();
			verify(redissonClient, times(1)).getLock(contains(RedisCacheConstants.LOCK_PREFIX));
			verify(lockMock, times(1)).unlock();
			verify(transactionMock, times(1)).commit();
		}

		@Test
		@DisplayName("[성공] 재고수가 캐싱되어 있지 않고, 메뉴ID에 락이 걸려있으면, 캐싱된 메뉴 재고 확인 재시도 후 받은 재고수를 반환한다.")
		void testUpdateStockNotCacheAndLocked() throws InterruptedException {
			// given
			Long menuId = 1L;
			Long initialStock = 10L;
			Long finalStock = 10L;  // 락이 해제된 후 설정될 재고

			RTransaction transactionMock = mock(RTransaction.class);
			RBucket bucketMock = mock(RBucket.class);
			RLock lockMock = mock(RLock.class);

			when(redissonClient.createTransaction(any(TransactionOptions.class))).thenReturn(transactionMock);
			when(transactionMock.getBucket(anyString())).thenReturn(bucketMock);
			when(bucketMock.isExists()).thenReturn(false).thenReturn(true);  // 첫 시도에는 없고, 두 번째 시도에는 있음
			when(redissonClient.getLock(anyString())).thenReturn(lockMock);
			when(lockMock.tryLock(anyLong(), anyLong(), any())).thenReturn(false);  // 첫 시도에는 실패
			when(bucketMock.get()).thenReturn(finalStock);

			// when
			Long result = redisMenuStockCacheService.updateStock(menuId, initialStock);

			// then
			assertEquals(finalStock, result, "반환된 재고 수가 예상과 다릅니다.");
			verify(redissonClient, times(1)).createTransaction(any(TransactionOptions.class));
			verify(transactionMock, times(2)).getBucket(contains(RedisCacheConstants.MENU_STOCK_PREFIX));
			verify(bucketMock, times(2)).isExists();
			verify(redissonClient, times(1)).getLock(contains(RedisCacheConstants.LOCK_PREFIX));
			verify(lockMock, times(1)).tryLock(anyLong(), anyLong(), any());
			verify(transactionMock, times(1)).commit();
		}

		@Test
		@DisplayName("[성공] 재고수가 캐싱되어 있으면 그 재고수를 반환한다.")
		void testUpdateStockCached() {
			// given
			Long menuId = 1L;
			Long stock = 10L;

			RTransaction transactionMock = mock(RTransaction.class);
			RBucket bucketMock = mock(RBucket.class);

			when(redissonClient.createTransaction(any(TransactionOptions.class))).thenReturn(transactionMock);
			when(transactionMock.getBucket(anyString())).thenReturn(bucketMock);
			when(bucketMock.isExists()).thenReturn(true);
			when(bucketMock.get()).thenReturn(stock);

			// when
			Long result = redisMenuStockCacheService.updateStock(menuId, stock);

			// then
			assertEquals(stock, result, "반환된 재고 수가 예상과 다릅니다.");
			verify(redissonClient, times(1)).createTransaction(any(TransactionOptions.class));
			verify(transactionMock, times(1)).getBucket(contains(RedisCacheConstants.MENU_STOCK_PREFIX));
			verify(bucketMock, times(1)).isExists();
			verify(bucketMock, times(1)).get();
			verify(transactionMock).commit();
		}

		@Test
		@DisplayName("[예외] RedissonClient 가 Transaction 생성에 실패하면 받은 재고수를 반환한다.")
		void testUpdateStockFailedToCreateTransaction() {
			// given
			Long menuId = 1L;
			Long stock = 10L;

			when(redissonClient.createTransaction(any(TransactionOptions.class))).thenThrow(
				new RuntimeException("트랜잭션 생성 실패"));

			// when
			Long result = redisMenuStockCacheService.updateStock(menuId, stock);

			// then
			assertEquals(stock, result, "반환된 재고 수가 예상과 다릅니다.");
		}

		@Test
		@DisplayName("[예외] RedissonClient 가 Lock 생성에 실패하면 받은 재고수를 반환한다.")
		void testUpdateStockFailedToCreateLock() {
			// given
			Long menuId = 1L;
			Long stock = 10L;

			RTransaction transactionMock = mock(RTransaction.class);
			RBucket rBucketMock = mock(RBucket.class);

			when(redissonClient.createTransaction(any(TransactionOptions.class))).thenReturn(transactionMock);
			when(transactionMock.getBucket(anyString())).thenReturn(rBucketMock);
			when(rBucketMock.isExists()).thenReturn(false);
			when(redissonClient.getLock(anyString())).thenThrow(
				new RuntimeException("락 생성 실패"));

			// when
			Long result = redisMenuStockCacheService.updateStock(menuId, stock);

			// then
			assertEquals(stock, result, "반환된 재고 수가 예상과 다릅니다.");
		}

		@Test
		@DisplayName("[예외] Redis 연결이 중간에 실패할 때 받은 재고수를 반환한다.")
		void testUpdateStockFailedToConnectRedis() {
			// given
			Long menuId = 1L;
			Long stock = 10L;

			RTransaction transactionMock = mock(RTransaction.class);

			when(redissonClient.createTransaction(any(TransactionOptions.class))).thenReturn(transactionMock);
			when(transactionMock.getBucket(anyString())).thenThrow(new TransactionException("트랜잭션 실패"));

			// when
			Long result = redisMenuStockCacheService.updateStock(menuId, stock);

			// then
			assertEquals(stock, result, "반환된 재고 수가 예상과 다릅니다.");
		}
	}
}
