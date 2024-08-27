package camp.woowak.lab.helper;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import redis.embedded.RedisServer;

public class EmbeddedRedisExtension implements BeforeAllCallback {

	private static RedisServer redisServer;
	private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
		EmbeddedRedisExtension.class);

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		if (redisServer == null) {
			redisServer = new RedisServer(6379);
			redisServer.start();
			context.getRoot().getStore(NAMESPACE).put("redisServer", redisServer);
		}
	}

	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", () -> "localhost");
		registry.add("spring.data.redis.port", () -> "6379");
	}
}