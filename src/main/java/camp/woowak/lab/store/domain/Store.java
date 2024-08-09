package camp.woowak.lab.store.domain;

import camp.woowak.lab.vendor.domain.Vendor;
import jakarta.persistence.*;

@Entity
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Vendor owner;
}
