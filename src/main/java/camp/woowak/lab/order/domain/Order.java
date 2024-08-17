package camp.woowak.lab.order.domain;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.store.domain.Store;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	public Order(Customer requester, Store store) {
		this.requester = requester;
		this.store = store;
	}
}
