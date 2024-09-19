package camp.woowak.lab.menu.service;

import java.util.HashMap;
import java.util.Map;

import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.infra.cache.redis.RedisCacheConstants;
import camp.woowak.lab.menu.repository.UpdateMenuStockDao;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuStockUpdateScheduleService {
	private final UpdateMenuStockDao updateMenuStockDao;
	private final RedissonClient redissonClient;

	// 5초마다 메뉴 재고를 RDB에 동기화
	@Scheduled(fixedRate = 1000 * 5)
	@Transactional
	public void syncMenuStockToRDB() {
		// redis 에서 메뉴 정보 조회
		Map<String, Long> menuStockMap = new HashMap<>();

		redissonClient.getKeys().getKeysStreamByPattern(RedisCacheConstants.MENU_STOCK_PREFIX + "*")
			.forEach(key -> {
				// 메뉴 정보 조회
				String menuId = key.split(":")[1];
				// 메뉴 재고 조회
				Long stock = redissonClient.getAtomicLong(key).get();
				menuStockMap.put(menuId, stock);
			});
		// 메뉴 재고를 RDB에 동기화
		updateMenuStockDao.updateMultipleMenuStocks(menuStockMap);
	}
}
