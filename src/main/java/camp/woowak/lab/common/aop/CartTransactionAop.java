package camp.woowak.lab.common.aop;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import camp.woowak.lab.cart.exception.RapidAddCartException;
import camp.woowak.lab.order.exception.DuplicatedOrderException;

@Aspect
@Component
@Order(1)
public class CartTransactionAop {
	private static final Logger log = LoggerFactory.getLogger(CartTransactionAop.class);

	@AfterThrowing(pointcut = "execution(* camp.woowak.lab.cart.service.CartService..*(..))", throwing = "ex")
	public void afterThrowingCart(ObjectOptimisticLockingFailureException ex) {
		log.info("카트에 상품을 담기 위한 낙관적 락을 획득하지 못했습니다.", ex);
		throw new RapidAddCartException("카트에 상품을 담기 위한 낙관적 락 획득에 실패했습니다.");
	}

	@AfterThrowing(pointcut = "execution(* camp.woowak.lab.order.service.OrderCreationService..*(..))", throwing = "ex")
	public void afterThrowingOrder(ObjectOptimisticLockingFailureException ex) {
		log.info("주문/결제를 하기 위한 낙관적 락을 획득하지 못했습니다.", ex);
		throw new DuplicatedOrderException("결제를 위한 낙관적 락 획득에 실패했습니다.");
	}
}
