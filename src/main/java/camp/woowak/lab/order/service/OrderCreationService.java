package camp.woowak.lab.order.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.cart.repository.CartRepository;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.order.domain.PriceChecker;
import camp.woowak.lab.order.domain.SingleStoreOrderValidator;
import camp.woowak.lab.order.domain.StockRequester;
import camp.woowak.lab.order.domain.WithdrawPointService;
import camp.woowak.lab.order.exception.EmptyCartException;
import camp.woowak.lab.order.repository.OrderRepository;
import camp.woowak.lab.order.service.command.OrderCreationCommand;
import camp.woowak.lab.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCreationService {
	private final OrderRepository orderRepository;
	private final CartRepository cartRepository;
	private final StoreRepository storeRepository;
	private final CustomerRepository customerRepository;
	private final SingleStoreOrderValidator singleStoreOrderValidator;
	private final StockRequester stockRequester;
	private final WithdrawPointService withdrawPointService;
	private final PriceChecker priceChecker;

	public Long create(OrderCreationCommand cmd) {
		UUID requesterId = cmd.requesterId();
		Customer requester = customerRepository.findByIdOrThrow(requesterId);

		Cart cart = cartRepository.findByCustomerId(requesterId.toString())
			.orElseThrow(() -> new EmptyCartException("구매자 " + requesterId + "가 비어있는 카트로 주문을 시도했습니다."));
		List<CartItem> cartItems = cart.getCartItems();

		Order savedOrder = orderRepository.save(
			new Order(requester, cartItems, singleStoreOrderValidator, stockRequester, priceChecker,
				withdrawPointService)
		);
		return savedOrder.getId();
	}
}
