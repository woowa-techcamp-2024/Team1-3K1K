package camp.woowak.lab.cart.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.cart.domain.Cart;

@DisplayName("InMemoryCartRepository 클래스")
class InMemoryCartRepositoryTest {
	private CartRepository repository;
	private static final String CUSTOMER_ID_EXIST = UUID.randomUUID().toString();
	private static final String CUSTOMER_ID_NOT_EXIST = UUID.randomUUID().toString();
	private Cart cart;

	@BeforeEach
	void setUp() {
		repository = new InMemoryCartRepository();
		cart = new Cart(CUSTOMER_ID_EXIST);
		repository.save(cart);
	}

	@AfterEach
	void clear(){
		repository.delete(cart);
	}

	@Nested
	@DisplayName("findByCustomerId 메서드")
	class FindByCustomerIdTest{
		@Test
		@DisplayName("customerId에 해당하는 Cart가 없으면 Optional(null)을 반환한다.")
		void NullReturnWhenCustomerIdNotExists(){
			//when
			Optional<Cart> foundCart = repository.findByCustomerId(CUSTOMER_ID_NOT_EXIST);

			//then
			assertThat(foundCart).isEmpty();
		}

		@Test
		@DisplayName("customerId에 해당하는 Cart가 있으면 해당 Cart를 반환한다.")
		void cartReturnWhenCustomerIdExists(){
			//when
			Optional<Cart> foundCart = repository.findByCustomerId(CUSTOMER_ID_EXIST);

			//then
			assertThat(foundCart).hasValue(cart);
		}
	}

	@Nested
	@DisplayName("save 메서드")
	class SaveTest{
		@Test
		@DisplayName("cart를 저장할 수 있다.")
		void saveTest(){
			//given
			Cart newCart = new Cart(CUSTOMER_ID_NOT_EXIST);

			//when
			Cart savedCart = repository.save(newCart);

			//then
			assertThat(newCart).isEqualTo(savedCart);
			Optional<Cart> foundCart = repository.findByCustomerId(CUSTOMER_ID_NOT_EXIST);
			assertThat(foundCart).hasValue(newCart);

			//tear down
			repository.delete(savedCart);
		}
	}

	@Nested
	@DisplayName("delete 메서드")
	class DeleteTest{
		@Test
		@DisplayName("존재하는 customerId의 cart를 삭제할 수 있다.")
		void deleteTest(){
			//when
			repository.delete(cart);

			//then
			Optional<Cart> foundCart = repository.findByCustomerId(CUSTOMER_ID_EXIST);
			assertThat(foundCart).isEmpty();
		}
	}
}