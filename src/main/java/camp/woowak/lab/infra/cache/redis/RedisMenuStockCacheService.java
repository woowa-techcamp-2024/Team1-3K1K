package camp.woowak.lab.infra.cache.redis;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import camp.woowak.lab.infra.cache.MenuStockCacheService;
import camp.woowak.lab.infra.cache.exception.CacheMissException;
import camp.woowak.lab.infra.cache.exception.RedisLockAcquisitionException;
import camp.woowak.lab.menu.exception.NotEnoughStockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMenuStockCacheService implements MenuStockCacheService {
	private final RedissonClient redissonClient;

	public Long updateStock(Long menuId, Long stock) {
		// redis 에 {메뉴ID: 재고} 가 있는 지 확인
		try {
			for (int retryCount = 0; retryCount < RedisCacheConstants.RETRY_COUNT; retryCount++) {
				// 캐시에 재고가 있는 지 확인
				RAtomicLong cachedStock = redissonClient.getAtomicLong(RedisCacheConstants.MENU_STOCK_PREFIX + menuId);
				if (cachedStock.isExists()) { // 캐시에 재고가 있는 경우: 캐시 반환
					return cachedStock.get();
				}

				// 캐시에 재고가 없는 경우: 락 확인
				// 락이 걸려있지 않은 경우 락 걸고 {메뉴ID:재고} 캐시 생성
				if (doWithLock(RedisCacheConstants.MENU_PREFIX + menuId,
					() -> cachedStock.set(stock))) { // action 성공 시 재시도 탈출
					break;
				}
			}

			return stock;
		} catch (Exception e) { // redis client 를 연결하지 못하면 넘어가도록 처리
			log.error("[Redis Cache Error]: {}", e.getMessage());
			return stock;
		}
	}

	@Override
	public Long addAtomicStock(Long menuId, int amount) {
		RAtomicLong cachedStock = redissonClient.getAtomicLong(RedisCacheConstants.MENU_STOCK_PREFIX + menuId);
		if (!cachedStock.isExists()) {    // 캐시 미스
			throw new CacheMissException("메뉴 재고 캐시 미스");
		}
		// TODO: 원자적 연산인지 확인 필요
		// cache 원자적 재고감소
		Long newStock = cachedStock.getAndAdd(-amount);

		if (newStock < 0) {
			// 원복
			cachedStock.getAndAdd(amount);
			throw new NotEnoughStockException("MenuId(" + menuId + ") 재고가 부족합니다.");
		}
		return newStock;
	}

	private void releaseLock(RLock lock) {
		if (lock.isHeldByCurrentThread()) {
			lock.unlock();
		}
	}

	public boolean doWithMenuIdLock(Long menuId, Runnable runnable) {
		RLock lock = redissonClient.getLock(RedisCacheConstants.LOCK_PREFIX + menuId);
	public boolean doWithLock(String key, Runnable runnable) {
		RLock lock = redissonClient.getLock(RedisCacheConstants.LOCK_PREFIX + key);
		try {
			boolean available = lock.tryLock(RedisCacheConstants.LOCK_WAIT_TIME,
				RedisCacheConstants.LOCK_LEASE_TIME,
				RedisCacheConstants.LOCK_TIME_UNIT); // TODO: 락 임대 시간 조정 필요

			if (!available) {
				return false;
			}
			runnable.run();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			releaseLock(lock);
		}
		return true;
	}
}
