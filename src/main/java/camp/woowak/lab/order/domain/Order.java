package camp.woowak.lab.order.domain;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.store.domain.Store;
import jakarta.persistence.*;

@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer requester;
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;
}
