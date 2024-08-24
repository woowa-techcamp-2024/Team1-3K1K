package camp.woowak.lab.infra.cache;

public interface MenuStockCacheService {
	Long updateStock(Long menuId, Long stock);

	Long addAtomicStock(Long menuId, int i);

	boolean doWithMenuIdLock(Long menuId, Runnable runnable);
}
