package camp.woowak.lab.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import camp.woowak.lab.order.service.command.RetrieveOrderListCommand;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.exception.NotEqualsOwnerException;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.web.dao.order.OrderDao;
import camp.woowak.lab.web.dao.order.OrderQuery;

@ExtendWith(MockitoExtension.class)
class RetrieveOrderListServiceTest {
	@InjectMocks
	private RetrieveOrderListService retrieveOrderListService;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private OrderDao orderDao;

	@Test
	@DisplayName("점주 주문 리스트 조회 테스트 - 성공")
	void testRetrieveOrderList() {
		// given
		given(orderDao.findAll(any(OrderQuery.class), any(Pageable.class))).willReturn(new PageImpl<>(
			List.of()));

		RetrieveOrderListCommand command = new RetrieveOrderListCommand(UUID.randomUUID(), PageRequest.of(0, 10));

		// when
		retrieveOrderListService.retrieveOrderListOfVendorStores(command);

		// then
		verify(orderDao).findAll(any(OrderQuery.class), any(Pageable.class));
	}

	@Test
	@DisplayName("점주 특정 매장 주문 리스트 조회 테스트 - 성공")
	void testRetrieveOrderListOfStore() {
		// given
		long storeId = 1L;
		UUID vendorId = UUID.randomUUID();
		Store fakeStore = Mockito.mock(Store.class);
		PageRequest pageRequest = PageRequest.of(0, 10);
		given(orderDao.findAll(any(OrderQuery.class), any(Pageable.class))).willReturn(new PageImpl<>(List.of()));
		given(storeRepository.findById(storeId)).willReturn(Optional.of(fakeStore));
		given(fakeStore.isOwnedBy(vendorId)).willReturn(true);

		RetrieveOrderListCommand command = new RetrieveOrderListCommand(storeId, vendorId, null, null, pageRequest);

		// when
		retrieveOrderListService.retrieveOrderListOfStore(command);

		// then
		verify(orderDao).findAll(any(OrderQuery.class), any(Pageable.class));
		verify(storeRepository).findById(storeId);
		verify(fakeStore).isOwnedBy(vendorId);
	}

	@Test
	@DisplayName("점주 특정 매장 주문 리스트 조회 테스트 - 실패(매장이 존재하지 않음)")
	void testRetrieveOrderListOfStoreFailByNotFoundStore() {
		// given
		long storeId = 1L;
		UUID vendorId = UUID.randomUUID();
		given(storeRepository.findById(storeId)).willReturn(Optional.empty());

		RetrieveOrderListCommand command = new RetrieveOrderListCommand(storeId, vendorId, null, null, PageRequest.of(0, 10));

		// when & then
		assertThrows(NotFoundStoreException.class,
			() -> retrieveOrderListService.retrieveOrderListOfStore(command));
	}

	@Test
	@DisplayName("점주 특정 매장 주문 리스트 조회 테스트 - 실패(매장의 주인이 아님)")
	void testRetrieveOrderListOfStoreFailByNotEqualsOwner() {
		// given
		long storeId = 1L;
		UUID vendorId = UUID.randomUUID();
		Store fakeStore = Mockito.mock(Store.class);
		given(storeRepository.findById(storeId)).willReturn(Optional.of(fakeStore));
		given(fakeStore.isOwnedBy(vendorId)).willReturn(false);

		RetrieveOrderListCommand command = new RetrieveOrderListCommand(storeId, vendorId, null, null, PageRequest.of(0, 10));

		// when & then
		assertThrows(NotEqualsOwnerException.class,
			() -> retrieveOrderListService.retrieveOrderListOfStore(command));
	}
}
