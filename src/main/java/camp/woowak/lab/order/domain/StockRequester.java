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

	public StockRequester(MenuRepository menuRepository, MenuStockCacheService menuStockCacheService) {
		this.menuRepository = menuRepository;
		this.menuStockCacheService = menuStockCacheService;
	}

	public List<CartItem> request(List<CartItem> cartItems) {
		List<CartItem> stockDecreaseSuccessCartItems = new ArrayList<>();

		// redis cache에 메뉴 재고를 요청
		for (CartItem cartItem : cartItems) {
			Long menuId = cartItem.getMenuId();
			try {
				menuStockCacheService.addAtomicStock(menuId, -cartItem.getAmount());
				stockDecreaseSuccessCartItems.add(cartItem);
			} catch (CacheMissException e) {// redis cache miss 면 findById 로 재고 확인 update
				menuStockCacheService.doWithLock(RedisCacheConstants.MENU_PREFIX + menuId, () -> {
					Menu menu = menuRepository.findById(menuId)
						.orElseThrow(() -> new MenuNotFoundException("메뉴가 존재하지 않습니다."));
					menuStockCacheService.updateStock(menuId, menu.getStockCount());
				});

			} catch (Exception e) {
				log.error("menuId: {} 재고 감소에 실패했습니다.", menuId, e);
				return stockDecreaseSuccessCartItems;
			}

		}

		List<Long> cartItemMenuIds = extractMenuIds(cartItems);

		List<Menu> allById = menuRepository.findAllByIdForUpdate(cartItemMenuIds);
		for (Menu menu : allById) {
			for (CartItem cartItem : cartItems) {
				if (cartItem.getMenuId().equals(menu.getId())) {
					menu.decrementStockCount(cartItem.getAmount());
				}
			}
		}

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
