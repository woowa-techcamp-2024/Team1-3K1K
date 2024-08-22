package camp.woowak.lab.order.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.order.service.command.RetrieveOrderListCommand;
import camp.woowak.lab.order.service.dto.OrderDTO;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.exception.NotEqualsOwnerException;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.web.dao.order.OrderDao;
import camp.woowak.lab.web.dao.order.OrderQuery;

@Service
@Transactional(readOnly = true)
public class RetrieveOrderListService {
	private final OrderDao orderDao;
	private final StoreRepository storeRepository;

	public RetrieveOrderListService(OrderDao orderDao, StoreRepository storeRepository) {
		this.orderDao = orderDao;
		this.storeRepository = storeRepository;
	}

	public Page<OrderDTO> retrieveOrderListOfVendorStores(RetrieveOrderListCommand command) {
		// 점주 매장 주문 조회 권한 검증은 필요없다.
		Page<Order> findOrders = orderDao.findAll(
			new OrderQuery(command.createdAfter(), command.createdBefore(), command.storeId(), command.vendorId()),
			command.pageable());
		return findOrders.map(OrderDTO::new);
	}

	/**
	 *
	 * @throws NotFoundStoreException 매장이 존재하지 않을 경우
	 * @throws NotEqualsOwnerException 매장의 주인이 아닐 경우
	 */
	public Page<OrderDTO> retrieveOrderListOfStore(RetrieveOrderListCommand command) {
		// 점주 매장 주문 조회 권한 검증
		// 점주가 소유한 매장인지 확인
		Store targetStore = storeRepository.findById(command.storeId())
			.orElseThrow(() -> new NotFoundStoreException("해당 매장이 존재하지 않습니다."));

		if (!targetStore.isOwnedBy(command.vendorId())) {
			throw new NotEqualsOwnerException(command.vendorId() + "는 " + targetStore.getId() + " 매장의 주인이 아닙니다.");
		}

		Page<Order> findOrders = orderDao.findAll(
			new OrderQuery(command.createdAfter(), command.createdBefore(), command.storeId(), command.vendorId()),
			command.pageable());

		return findOrders.map(OrderDTO::new);
	}
}
