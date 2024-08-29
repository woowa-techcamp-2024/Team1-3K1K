package camp.woowak.lab.cart.persistence.redis.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.domain.vo.CartItem;

@SpringBootTest
@TestPropertySources({
	@TestPropertySource(properties = "cart.dao=redis"),
	@TestPropertySource(properties = "cart.repository=redis")
})
public class RedisCartRepositoryTest {
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private RedisCartRepository repository;

	private static final String CUSTOMER_ID_EXIST = UUID.randomUUID().toString();
	private static final String CUSTOMER_ID_NOT_EXIST = UUID.randomUUID().toString();
	private Cart cart;

	@BeforeEach
	void setUp() {
		List<CartItem> cartItemList = List.of(new CartItem(1L, 1L, 1),
											  new CartItem(2L, 2L, 2),
											  new CartItem(3L, 3L, 3));
		cart = new Cart(CUSTOMER_ID_EXIST, cartItemList);
		repository.save(cart);
	}

	@AfterEach
	void clear() {
		repository.delete(cart);
		redisTemplate.execute((RedisConnection connection) -> {
			connection.serverCommands().flushAll();
			return null;
		});
	}

	@Nested
	@DisplayName("findByCustomerId 메서드")
	class FindByCustomerIdTest {
		@Test
		@DisplayName("customerId에 해당하는 Cart가 없으면 Optional(null)을 반환한다.")
		void NullReturnWhenCustomerIdNotExists() {
			//when
			Optional<Cart> foundCart = repository.findByCustomerId(CUSTOMER_ID_NOT_EXIST);

			//then
			assertThat(foundCart).isEmpty();
		}

		@Test
		@DisplayName("customerId에 해당하는 Cart가 있으면 해당 Cart를 반환한다.")
		void cartReturnWhenCustomerIdExists() {
			//when
			Optional<Cart> foundCart = repository.findByCustomerId(CUSTOMER_ID_EXIST);

			//then
			assertThat(foundCart).isPresent();
			assertCart(cart, foundCart.get());
		}
	}

	@Nested
	@DisplayName("save 메서드")
	class SaveTest {
		@Test
		@DisplayName("cart를 저장할 수 있다.")
		void saveTest() {
			//given
			Cart newCart = new Cart(CUSTOMER_ID_NOT_EXIST);

			//when
			Cart savedCart = repository.save(newCart);

			//then
			assertCart(savedCart, newCart);
			Optional<Cart> foundCart = repository.findByCustomerId(CUSTOMER_ID_NOT_EXIST);
			assertThat(foundCart).isPresent();
			Cart foundedCart = foundCart.get();
			assertCart(savedCart, foundedCart);
		}
	}

	@Nested
	@DisplayName("delete 메서드")
	class DeleteTest {
		@Test
		@DisplayName("존재하는 customerId의 cart를 삭제할 수 있다.")
		void deleteTest() {
			//when
			repository.delete(cart);

			//then
			Optional<Cart> foundCart = repository.findByCustomerId(CUSTOMER_ID_EXIST);
			assertThat(foundCart).isEmpty();
		}
	}

	private void assertCart(Cart expected, Cart actual) {
		assertThat(actual.getCustomerId()).isEqualTo(expected.getCustomerId());
		assertThat(actual.getCartItems().size()).isEqualTo(expected.getCartItems().size());
		for (int i = 0; i < expected.getCartItems().size(); i++) {
			assertThat(actual.getCartItems().get(i).getAmount()).isEqualTo(expected.getCartItems().get(i).getAmount());
			assertThat(actual.getCartItems().get(i).getMenuId()).isEqualTo(expected.getCartItems().get(i).getMenuId());
			assertThat(actual.getCartItems().get(i).getStoreId()).isEqualTo(
				expected.getCartItems().get(i).getStoreId());
		}
	}
}