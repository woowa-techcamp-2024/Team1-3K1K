package camp.woowak.lab.payment.service;

import java.util.List;

import org.springframework.stereotype.Component;

import camp.woowak.lab.payment.domain.OrderPayment;

@Component
public class SettlementAmountCalculator {

	public Long calculate(final List<OrderPayment> orderPayments) {
		Long totalOrderPrice = calculateTotalOrderPrice(orderPayments);
		Long commission = calculateCommission(totalOrderPrice);

		return totalOrderPrice - commission;
	}

	private Long calculateTotalOrderPrice(final List<OrderPayment> orderPayments) {
		Long totalOrderPrice = 0L;
		for (OrderPayment orderPayment : orderPayments) {
			totalOrderPrice += orderPayment.calculateOrderPrice();
		}
		return totalOrderPrice;
	}

	private Long calculateCommission(final Long totalOrderPrice) {
		return (long)(totalOrderPrice * 0.05);
	}

}
