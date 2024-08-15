package camp.woowak.lab.menu.domain;

import camp.woowak.lab.store.domain.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Menu {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;

	@Column(nullable = false)
	private String name;

	@Column
	private int price;

	@Column
	private int quantity;

	public Menu(Store store, String name, int price, int quantity) {
		this.store = store;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
	}
}
