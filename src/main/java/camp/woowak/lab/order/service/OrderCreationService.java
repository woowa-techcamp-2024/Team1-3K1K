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
import camp.woowak.lab.infra.aop.DistributedLock;
import camp.woowak.lab.menu.exception.NotEnoughStockException;
import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.order.domain.OrderFactory;
import camp.woowak.lab.order.exception.DuplicatedOrderException;
import camp.woowak.lab.order.exception.EmptyCartException;
import camp.woowak.lab.order.exception.MinimumOrderPriceNotMetException;
import camp.woowak.lab.order.exception.MultiStoreOrderException;
import camp.woowak.lab.order.exception.NotFoundMenuException;
import camp.woowak.lab.order.repository.OrderRepository;
import camp.woowak.lab.order.service.command.OrderCreationCommand;
import camp.woowak.lab.payaccount.exception.InsufficientBalanceException;
import camp.woowak.lab.payaccount.exception.NotFoundAccountException;
import camp.woowak.lab.payment.domain.OrderPayment;
import camp.woowak.lab.payment.domain.OrderPaymentStatus;
import camp.woowak.lab.payment.repository.OrderPaymentRepository;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCreationService {
	private final OrderRepository orderRepository;
	private final CartRepository cartRepository;
	private final CustomerRepository customerRepository;
	private final OrderPaymentRepository orderPaymentRepository;

	private final OrderFactory orderFactory;

	/**
	 * @throws EmptyCartException               카트가 비어 있는 경우
	 * @throws NotFoundStoreException           가게가 조회되지 않는 경우
	 * @throws MultiStoreOrderException         여러 가게의 메뉴를 주문한 경우
	 * @throws NotEnoughStockException          메뉴의 재고가 부족한 경우
	 * @throws NotFoundMenuException            주문한 메뉴가 조회되지 않는 경우
	 * @throws MinimumOrderPriceNotMetException 가게의 최소 주문금액보다 적은 금액을 주문한 경우
	 * @throws NotFoundAccountException         구매자의 계좌가 조회되지 않는 경우
	 * @throws InsufficientBalanceException     구매자의 계좌에 잔액이 충분하지 않은 경우
	 */
	@DistributedLock(key = "#cmd.requesterId()", throwable = DuplicatedOrderException.class, exceptionMessage = "중복된 요청입니다.", waitTime = 0L)
	public Long create(final OrderCreationCommand cmd) {
		UUID requesterId = cmd.requesterId();
		Customer requester = customerRepository.findByIdOrThrow(requesterId);
		Cart cart = cartRepository.findByCustomerId(requesterId.toString())
			.orElseThrow(() -> new EmptyCartException("구매자 " + requesterId + "가 비어있는 카트로 주문을 시도했습니다."));
		List<CartItem> cartItems = cart.getCartItems();

		Order savedOrder = orderRepository.save(
			orderFactory.createOrder(requester, cartItems)
		);

		saveOrderPayment(savedOrder);
		cartRepository.delete(cart);
		return savedOrder.getId();
	}

	private void saveOrderPayment(final Order savedOrder) {
		orderPaymentRepository.save(
			new OrderPayment(savedOrder, savedOrder.getRequester(), savedOrder.getVendor(),
				OrderPaymentStatus.ORDER_SUCCESS, savedOrder.getCreatedAt()
			)
		);
	}
}
