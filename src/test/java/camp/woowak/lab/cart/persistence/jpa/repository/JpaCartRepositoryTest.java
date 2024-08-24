package camp.woowak.lab.cart.persistence.jpa.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionTemplate;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.repository.CartRepository;

@DataJpaTest
class JpaCartRepositoryTest {
	@Autowired
	private TransactionTemplate transactionTemplate;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private CartEntityRepository cartEntityRepository;

	private UUID fakeCustomerId;

	@TestConfiguration
	static class TestContextConfiguration {
		@Bean
		public CartRepository cartRepository(CartEntityRepository cartEntityRepository) {
			return new JpaCartRepository(cartEntityRepository);
		}
	}

	@BeforeEach
	void setUp() {
		fakeCustomerId = UUID.randomUUID();
		cartRepository.save(new Cart(fakeCustomerId.toString()));
	}

	@Nested
	@DisplayName("[findByCustomerId]")
	class FindByCustomerIdIs {
		@Test
		@DisplayName("저장된 CartEntity가 있는 경우")
		void returnOptionalWhenCartEntityIsFound() {
			Optional<Cart> findCart = transactionTemplate.execute(
				(status) -> cartRepository.findByCustomerId(fakeCustomerId.toString()));
			assertThat(findCart.isPresent()).isTrue();
			assertThat(findCart.get().getCustomerId()).isEqualTo(fakeCustomerId.toString());
		}

		@Test
		@DisplayName("저장된 CartEntity가 없는 경우")
		void returnEmptyOptionalWhenCartEntityIsNotFound() {
			cartEntityRepository.deleteAll();
			Optional<Cart> findCart = transactionTemplate.execute(
				(status) -> cartRepository.findByCustomerId(fakeCustomerId.toString()));
			assertThat(findCart.isPresent()).isFalse();
		}
	}

	@Nested
	@DisplayName("[save]")
	class SaveIs {
		@Test
		@DisplayName("[예외] 같은 구매자 id로 카트를 저장하면")
		void duplicateCustomerId() {
			Assertions.assertThrows(DataIntegrityViolationException.class,
				() -> cartRepository.save(new Cart(fakeCustomerId.toString(), List.of())));
		}

		@Test
		@DisplayName("[성공] 중복되지 않은 구매자 id로 카트를 저장하면")
		void success() {
			UUID newFakeCustomerId = UUID.randomUUID();
			Cart save = cartRepository.save(new Cart(newFakeCustomerId.toString()));
			assertThat(save.getCustomerId()).isEqualTo(newFakeCustomerId.toString());
		}
	}
}
