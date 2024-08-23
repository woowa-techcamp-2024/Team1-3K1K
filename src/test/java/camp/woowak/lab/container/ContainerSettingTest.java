package camp.woowak.lab.container;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@Import({ContainerSettingTest.TestConfigurationWithRedis.class})
public class ContainerSettingTest {
	@Container
	private static final GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse("redis:6-alpine"))
		.withExposedPorts(6379);

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@TestConfiguration
	public static class TestConfigurationWithRedis {
		@Bean
		public RedisConnectionFactory redisConnectionFactory() {
			int port = container.getFirstMappedPort();
			String host = container.getHost();
			return new LettuceConnectionFactory(host, port);
		}

		@Bean
		public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
			RedisTemplate<String, Object> template = new RedisTemplate<>();
			template.setConnectionFactory(redisConnectionFactory);

			template.setKeySerializer(new StringRedisSerializer());
			template.setValueSerializer(new StringRedisSerializer());

			return template;
		}
	}

	@Disabled
	@Test
	void testSimplePutAndGet() {
		redisTemplate.opsForValue().set("key", "value");

		String o = (String)redisTemplate.opsForValue().get("key");

		assertThat(o).isEqualTo("value");
	}
}
