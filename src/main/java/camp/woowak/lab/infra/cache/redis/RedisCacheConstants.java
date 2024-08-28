package camp.woowak.lab.infra.cache.redis;

import java.util.concurrent.TimeUnit;

public final class RedisCacheConstants {
	public static final String MENU_STOCK_PREFIX = "menu-stock:";
	public static final String LOCK_PREFIX = "lock-";
	public static final int RETRY_COUNT = 10;
	public static final long LOCK_WAIT_TIME = 10L;
	public static final long LOCK_LEASE_TIME = 3000L;
	public static final TimeUnit LOCK_TIME_UNIT = TimeUnit.MICROSECONDS;
	public static final String MENU_PREFIX = "menu:";
}
