package camp.woowak.lab.infra.cache;

public class FakeMenuStockCacheService implements MenuStockCacheService {

	@Override
	public Long updateStock(Long menuId, Long stock) {
		return stock;
	}

	@Override
	public Long addAtomicStock(Long menuId, int i) {
		return 0L;
	}

	@Override
	public boolean doWithMenuIdLock(Long menuId, Runnable runnable) {

		return false;
	}
}
