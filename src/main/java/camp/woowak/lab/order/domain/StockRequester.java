package camp.woowak.lab.order.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.cart.exception.MenuNotFoundException;
import camp.woowak.lab.infra.cache.MenuStockCacheService;
import camp.woowak.lab.infra.cache.exception.CacheMissException;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.exception.NotEnoughStockException;
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
	@Transactional
	public List<CartItem> request(List<CartItem> cartItems) {
		List<CartItem> stockDecreaseSuccessCartItems = new ArrayList<>();

		// 메뉴 한개씩 돌면서 레디스 확인하고 디비 바꿔주고
		// 레디스에 모두 돌고 모두 성공하면 한번에 디비에 업데이트
		// redis cache에 메뉴 재고를 요청

		for (CartItem cartItem : cartItems) {
			Long menuId = cartItem.getMenuId();
			for (int retryCount = 0; retryCount < RETRY_COUNT; retryCount++) {
				try {
					menuStockCacheService.addAtomicStock(menuId, -cartItem.getAmount());
					stockDecreaseSuccessCartItems.add(cartItem);
					break; // 재고 감소 성공 시 탈출
				} catch (CacheMissException e) { // redis cache miss 면 findById 로 재고 확인 update
					// (1) cache miss 시 RDB 에 접근해서 재고를 가져와야해요
					// (2) 가져온 재고를 업데이트
					Menu menu = menuRepository.findById(menuId)
						.orElseThrow(() -> new MenuNotFoundException("메뉴가 존재하지 않습니다."));    // 준기: 10, 현수: 8 -> redis: 8
					menuStockCacheService.updateStock(menuId, menu.getStockCount());    // lock
				} catch (NotEnoughStockException e) {
					log.error("menuId: {} 재고가 부족합니다.", menuId, e);
					rollback(stockDecreaseSuccessCartItems);
					throw e;
				} catch (Exception e) {
					log.error("menuId: {} 재고 감소에 실패했습니다.", menuId, e);
					return stockDecreaseSuccessCartItems;
				}
			}
		}
		// TODO: RDB 비동기 재고 업데이트
		for (var cartItem : stockDecreaseSuccessCartItems) {
			menuRepository.decreaseStock(cartItem.getMenuId(), cartItem.getAmount());
		}

		return stockDecreaseSuccessCartItems;
	}

	public void rollback(List<CartItem> stockDecreaseSuccessCartItems) {
		for (CartItem cartItem : stockDecreaseSuccessCartItems) {
			menuStockCacheService.addAtomicStock(cartItem.getMenuId(), cartItem.getAmount());
		}
	}
}
