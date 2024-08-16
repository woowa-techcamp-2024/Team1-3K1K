package camp.woowak.lab.order.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.exception.NotFoundAccountException;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;

@Component
public class WithdrawPointService {
	private final PayAccountRepository payAccountRepository;

	public WithdrawPointService(PayAccountRepository payAccountRepository) {
		this.payAccountRepository = payAccountRepository;
	}

	public List<OrderItem> withdraw(Customer customer, List<OrderItem> orderItems) {
		Optional<PayAccount> findPayAccount = payAccountRepository.findByCustomerIdForUpdate(customer.getId());
		if (findPayAccount.isEmpty()) {
			throw new NotFoundAccountException("주문을 처리할 계좌가 생성되지 않았습니다.");
		}
		PayAccount payAccount = findPayAccount.get();
		int totalPrice = 0;
		for (OrderItem orderItem : orderItems) {
			totalPrice += orderItem.getTotalPrice();
		}
		payAccount.withdraw(totalPrice);
		return orderItems;
	}
}
