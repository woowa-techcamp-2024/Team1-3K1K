package camp.woowak.lab.order.domain;

import java.util.List;

import org.springframework.stereotype.Component;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.infra.date.DateTimeProvider;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.store.domain.Store;

@Component
public class OrderFactory {
	private final SingleStoreOrderValidator singleStoreOrderValidator;
	private final StockRequester stockRequester;
	private final PriceChecker priceChecker;
	private final WithdrawPointService withdrawPointService;
	private final DateTimeProvider dateTimeProvider;

	public OrderFactory(SingleStoreOrderValidator singleStoreOrderValidator,
						StockRequester stockRequester,
						PriceChecker priceChecker,
						WithdrawPointService withdrawPointService, DateTimeProvider dateTimeProvider) {
		this.singleStoreOrderValidator = singleStoreOrderValidator;
		this.stockRequester = stockRequester;
		this.priceChecker = priceChecker;
		this.withdrawPointService = withdrawPointService;
		this.dateTimeProvider = dateTimeProvider;
	}

	public Order createOrder(Customer requester, List<CartItem> cartItems) {
		Store store = singleStoreOrderValidator.check(cartItems);

		List<CartItem> stockDecreaseSuccessCartItems = null;
		try {
			stockDecreaseSuccessCartItems = stockRequester.request(cartItems);
			List<OrderItem> orderItems = priceChecker.check(store, cartItems);
			withdrawPointService.withdraw(requester, orderItems);

			return new Order(requester, store, orderItems, dateTimeProvider.now());
		} catch (Exception e) {
			if (stockDecreaseSuccessCartItems != null) {
				stockRequester.rollback(stockDecreaseSuccessCartItems);
			}
			throw e;
		}
	}
}
