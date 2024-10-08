package camp.woowak.lab.order.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.menu.exception.NotEnoughStockException;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.order.exception.EmptyCartException;
import camp.woowak.lab.order.exception.MinimumOrderPriceNotMetException;
import camp.woowak.lab.order.exception.MultiStoreOrderException;
import camp.woowak.lab.order.exception.NotFoundMenuException;
import camp.woowak.lab.payaccount.exception.InsufficientBalanceException;
import camp.woowak.lab.payaccount.exception.NotFoundAccountException;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import camp.woowak.lab.vendor.domain.Vendor;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ORDERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Customer requester;

	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;

	@CollectionTable(name = "ORDER_ITEMS", joinColumns = @JoinColumn(name = "order_id"))
	@ElementCollection(fetch = FetchType.EAGER)
	private List<OrderItem> orderItems = new ArrayList<>();

	private LocalDateTime createdAt;

	/**
	 * @throws EmptyCartException 카트가 비어 있는 경우
	 * @throws NotFoundStoreException 가게가 조회되지 않는 경우
	 * @throws MultiStoreOrderException 여러 가게의 메뉴를 주문한 경우
	 * @throws NotEnoughStockException 메뉴의 재고가 부족한 경우
	 * @throws NotFoundMenuException 주문한 메뉴가 조회되지 않는 경우
	 * @throws MinimumOrderPriceNotMetException 가게의 최소 주문금액보다 적은 금액을 주문한 경우
	 * @throws NotFoundAccountException 구매자의 계좌가 조회되지 않는 경우
	 * @throws InsufficientBalanceException 구매자의 계좌에 잔액이 충분하지 않은 경우
	 */
	public Order(Customer requester, List<CartItem> cartItems,
				 SingleStoreOrderValidator singleStoreOrderValidator,
				 StockRequester stockRequester, PriceChecker priceChecker, WithdrawPointService withdrawPointService,
				 LocalDateTime createdAt) {
		Store store = singleStoreOrderValidator.check(cartItems);

		List<CartItem> stockDecreaseSuccessCartItems = null;
		try {
			stockDecreaseSuccessCartItems = stockRequester.request(cartItems);
			List<OrderItem> orderItems = priceChecker.check(store, cartItems);
			withdrawPointService.withdraw(requester, orderItems);
			this.requester = requester;
			this.store = store;
			this.orderItems = orderItems;
			this.createdAt = createdAt;
		} catch (Exception e) {
			if (stockDecreaseSuccessCartItems != null) {
				stockRequester.rollback(stockDecreaseSuccessCartItems);
			}
			throw e;
		}
	}

	public Customer getRequester() {
		return requester;
	}

	public Store getStore() {
		return store;
	}

	public List<OrderItem> getOrderItems() {
		return Collections.unmodifiableList(orderItems);
	}

	public Vendor getVendor() {
		return store.getOwner();
	}

	public Long calculateTotalPrice() {
		Long totalPrice = 0L;
		for (OrderItem orderItem : orderItems) {
			totalPrice += orderItem.getTotalPrice();
		}
		return totalPrice;
	}

}
