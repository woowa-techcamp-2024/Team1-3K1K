package camp.woowak.lab.payment.domain;

import java.time.LocalDateTime;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.vendor.domain.Vendor;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderPayment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sendor_id", nullable = false)
	private Customer sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recipient_id", nullable = false)
	private Vendor recipient;

	@Enumerated(value = EnumType.STRING)
	private OrderPaymentStatus orderPaymentStatus;

	private LocalDateTime createdAt;

	public OrderPayment(Order order, Customer sender, Vendor recipient,
						OrderPaymentStatus orderPaymentStatus, LocalDateTime createdAt
	) {
		this.order = order;
		this.sender = sender;
		this.recipient = recipient;
		this.orderPaymentStatus = orderPaymentStatus;
		this.createdAt = createdAt;
	}

	public void validateReadyToAdjustment(final Vendor adjustmentTarget) {
		if (isEqualsRecipient(adjustmentTarget) && orderPaymentStatusIsSuccess()) {
			return;
		}
		throw new IllegalArgumentException();
	}

	private boolean isEqualsRecipient(Vendor recipient) {
		return this.recipient.equals(recipient);
	}

	private boolean orderPaymentStatusIsSuccess() {
		return this.orderPaymentStatus.equals(OrderPaymentStatus.ORDER_SUCCESS);
	}

}
