package camp.woowak.lab.order.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.order.exception.EmptyCartException;
import camp.woowak.lab.order.exception.MultiStoreOrderException;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import camp.woowak.lab.store.repository.StoreRepository;

class SingleStoreOrderValidatorTest {

	private SingleStoreOrderValidator validator;
	private StoreRepository storeRepository;

	@BeforeEach
	void setUp() {
		storeRepository = mock(StoreRepository.class);
		validator = new SingleStoreOrderValidator(storeRepository);
	}

	@Test
	void check_NullCartItems_ThrowsEmptyCartException() {
		// Given
		Store store = mock(Store.class);

		// When & Then
		assertThrows(EmptyCartException.class, () -> validator.check(null));
	}

	@Test
	void check_EmptyCart_ThrowsEmptyCartException() {
		// When & Then
		assertThrows(EmptyCartException.class, () -> validator.check(List.of()));
	}

	@Test
	void check_StoreNotFound_ThrowsNotFoundStoreException() {
		// Given
		CartItem cartItem = mock(CartItem.class);
		when(cartItem.getStoreId()).thenReturn(1L);
		when(storeRepository.findById(1L)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(NotFoundStoreException.class, () -> validator.check(List.of(cartItem)));
	}

	@Test
	void check_ValidCart_ReturnsStore() {
		// Given
		Store store = mock(Store.class);
		when(store.getId()).thenReturn(1L);
		when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

		CartItem cartItem1 = mock(CartItem.class);
		CartItem cartItem2 = mock(CartItem.class);
		when(cartItem1.getStoreId()).thenReturn(1L);
		when(cartItem2.getStoreId()).thenReturn(1L);

		// When
		Store result = validator.check(List.of(cartItem1, cartItem2));

		// Then
		assertEquals(store, result);
		verify(storeRepository).findById(1L);
	}

	@Test
	void check_MultiStoreOrder_ThrowsMultiStoreOrderException() {
		// Given
		Store store = mock(Store.class);
		when(store.getId()).thenReturn(1L);
		when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

		CartItem cartItem1 = mock(CartItem.class);
		CartItem cartItem2 = mock(CartItem.class);
		when(cartItem1.getStoreId()).thenReturn(1L);
		when(cartItem2.getStoreId()).thenReturn(2L);

		// When & Then
		assertThrows(MultiStoreOrderException.class, () -> validator.check(List.of(cartItem1, cartItem2)));
	}
}
