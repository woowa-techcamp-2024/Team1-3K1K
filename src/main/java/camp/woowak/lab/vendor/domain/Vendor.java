package camp.woowak.lab.vendor.domain;

import camp.woowak.lab.payaccount.domain.PayAccount;
import jakarta.persistence.*;

@Entity
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    private PayAccount payAccount;
}
