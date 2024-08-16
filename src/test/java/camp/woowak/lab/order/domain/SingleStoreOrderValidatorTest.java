package camp.woowak.lab.order.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.order.exception.EmptyCartException;
import camp.woowak.lab.order.exception.MultiStoreOrderException;
import camp.woowak.lab.store.domain.Store;

class SingleStoreOrderValidatorTest {

	private SingleStoreOrderValidator validator;

	@BeforeEach
	void setUp() {
		validator = new SingleStoreOrderValidator();
	}

	@Test
	void check_NullCartItems_ThrowsEmptyCartException() {
		// Given
		Store store = mock(Store.class);

		// When & Then
		assertThrows(EmptyCartException.class, () -> validator.check(store, null));
	}

	@Test
	void check_EmptyCartItems_ThrowsEmptyCartException() {
		// Given
		Store store = mock(Store.class);

		// When & Then
		assertThrows(EmptyCartException.class, () -> validator.check(store, List.of()));
	}

	@Test
	void check_AllItemsBelongToSameStore_NoExceptionThrown() {
		// Given
		Store store = mock(Store.class);
		Long storeId = 1L;
		when(store.getId()).thenReturn(storeId);

		CartItem item1 = mock(CartItem.class);
		CartItem item2 = mock(CartItem.class);
		when(item1.getStoreId()).thenReturn(storeId);
		when(item2.getStoreId()).thenReturn(storeId);

		List<CartItem> cartItems = List.of(item1, item2);

		// When & Then
		assertDoesNotThrow(() -> validator.check(store, cartItems));
	}

	@Test
	void check_ItemsFromDifferentStores_ThrowsMultiStoreOrderException() {
		// Given
		Store store = mock(Store.class);
		Long storeId1 = 1L;
		Long storeId2 = 2L;
		when(store.getId()).thenReturn(storeId1);

		CartItem item1 = mock(CartItem.class);
		CartItem item2 = mock(CartItem.class);
		when(item1.getStoreId()).thenReturn(storeId1);
		when(item2.getStoreId()).thenReturn(storeId2);

		List<CartItem> cartItems = List.of(item1, item2);

		// When & Then
		assertThrows(MultiStoreOrderException.class, () -> validator.check(store, cartItems));
	}
}
