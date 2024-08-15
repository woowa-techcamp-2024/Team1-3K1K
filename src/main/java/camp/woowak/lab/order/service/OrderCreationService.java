package camp.woowak.lab.order.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.repository.CartRepository;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.order.domain.CompositeOrderValidator;
import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.order.domain.OrderValidator;
import camp.woowak.lab.order.exception.EmptyCartException;
import camp.woowak.lab.order.repository.OrderRepository;
import camp.woowak.lab.order.service.command.OrderCreationCommand;

@Service
@Transactional
public class OrderCreationService {
	private final OrderRepository orderRepository;
	private final CartRepository cartRepository;
	private final CustomerRepository customerRepository;
	private final CompositeOrderValidator orderValidators;

	public OrderCreationService(OrderRepository orderRepository, CartRepository cartRepository,
								CustomerRepository customerRepository, List<OrderValidator> orderValidators) {
		this.orderRepository = orderRepository;
		this.cartRepository = cartRepository;
		this.customerRepository = customerRepository;
		this.orderValidators = new CompositeOrderValidator(orderValidators);
	}

	public Long create(OrderCreationCommand cmd) {
		UUID requesterId = cmd.requesterId();
		Customer requester = customerRepository.findByIdOrThrow(requesterId);
		Optional<Cart> findCart = cartRepository.findByCustomerId(requesterId.toString());
		Cart cart;
		if (findCart.isEmpty() || (cart = findCart.get()).getMenuList().isEmpty()) {
			throw new EmptyCartException("구매자 " + cmd.requesterId() + "가 비어있는 카트로 주문을 시도했습니다.");
		}
		Order savedOrder = orderRepository.save(new Order(requester, cart.getMenuList(), orderValidators));
		return savedOrder.getId();
	}
}
