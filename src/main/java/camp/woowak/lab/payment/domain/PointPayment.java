package camp.woowak.lab.payment.domain;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.vendor.domain.Vendor;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class PointPayment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	private Order order;
	@ManyToOne(fetch = FetchType.LAZY)
	private Customer sender;
	@ManyToOne(fetch = FetchType.LAZY)
	private Vendor recipient;
}
