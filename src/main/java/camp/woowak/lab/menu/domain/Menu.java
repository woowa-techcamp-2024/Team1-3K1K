package camp.woowak.lab.menu.domain;

import camp.woowak.lab.store.domain.Store;
import jakarta.persistence.*;

@Entity
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;
}
