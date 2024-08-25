package camp.woowak.lab.order.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.cart.exception.MenuNotFoundException;
import camp.woowak.lab.infra.cache.MenuStockCacheService;
import camp.woowak.lab.infra.cache.exception.CacheMissException;
import camp.woowak.lab.infra.cache.redis.RedisCacheConstants;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.repository.MenuRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StockRequester {
	private final MenuRepository menuRepository;
	private final MenuStockCacheService menuStockCacheService;
	private final static int RETRY_COUNT = 10;

	public StockRequester(MenuRepository menuRepository, MenuStockCacheService menuStockCacheService) {
		this.menuRepository = menuRepository;
		this.menuStockCacheService = menuStockCacheService;
	}

	/**
	 *
	 * @throw NotEnoughStockException 재고가 부족한 경우
	 */
	public List<CartItem> request(List<CartItem> cartItems) {
		List<CartItem> stockDecreaseSuccessCartItems = new ArrayList<>();

		// redis cache에 메뉴 재고를 요청
		for (CartItem cartItem : cartItems) {
			Long menuId = cartItem.getMenuId();
			Long newStock = null;
			for (int retryCount = 0; retryCount < RETRY_COUNT; retryCount++) {
				try {
					newStock = menuStockCacheService.addAtomicStock(menuId, -cartItem.getAmount());
					stockDecreaseSuccessCartItems.add(cartItem);
					break; // 재고 감소 성공 시 탈출
				} catch (CacheMissException e) { // redis cache miss 면 findById 로 재고 확인 update
					// (1) cache miss 시 RDB 에 접근해서 재고를 가져와야해요
					// (2) 가져온 재고를 업데이트
					Menu menu = menuRepository.findById(menuId)
						.orElseThrow(() -> new MenuNotFoundException("메뉴가 존재하지 않습니다."));    // 준기: 10, 현수: 8 -> redis: 8
					menuStockCacheService.updateStock(menuId, menu.getStockCount());    // lock
				} catch (NotEnoughStockException e) {
					throw e;
				} catch (Exception e) {
					log.error("menuId: {} 재고 감소에 실패했습니다.", menuId, e);
					return stockDecreaseSuccessCartItems;
				} finally {
					if (newStock != null) {
						menuRepository.decreaseStock(menuId, newStock);
					}
				}
			}
		}
		// TODO: RDB 비동기 재고 업데이트

		return stockDecreaseSuccessCartItems;
	}

	private List<Long> extractMenuIds(List<CartItem> cartItems) {
		return cartItems.stream().map(CartItem::getMenuId).toList();
	}

	public void rollback(List<CartItem> stockDecreaseSuccessCartItems) {
		for (CartItem cartItem : stockDecreaseSuccessCartItems) {
			menuStockCacheService.addAtomicStock(cartItem.getMenuId(), cartItem.getAmount());
		}
	}
}
