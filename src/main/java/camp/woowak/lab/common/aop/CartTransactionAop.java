package camp.woowak.lab.common.aop;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import camp.woowak.lab.cart.exception.RapidAddCartException;
import camp.woowak.lab.order.exception.DuplicatedOrderException;

@Aspect
@Component
@Order(1)
public class CartTransactionAop {
	@AfterThrowing(pointcut = "execution(* camp.woowak.lab.cart.service.CartService..*(..))", throwing = "ex")
	public void afterThrowingCart(ObjectOptimisticLockingFailureException ex) {
		throw new RapidAddCartException("카트에 상품을 담기 위한 낙관적 락 획득에 실패했습니다.");
	}

	@AfterThrowing(pointcut = "execution(* camp.woowak.lab.order.service.OrderCreationService..*(..))", throwing = "ex")
	public void afterThrowingOrder(ObjectOptimisticLockingFailureException ex) {
		throw new DuplicatedOrderException("결제를 위한 낙관적 락 획득에 실패했습니다.");
	}
}
