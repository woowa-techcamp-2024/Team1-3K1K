package camp.woowak.lab.menu.domain;

import org.hibernate.annotations.DynamicUpdate;

import camp.woowak.lab.menu.exception.InvalidMenuPriceUpdateException;
import camp.woowak.lab.menu.exception.NotEnoughStockException;
import camp.woowak.lab.store.domain.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@DynamicUpdate
public class Menu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_category_id", nullable = false)
	private MenuCategory menuCategory;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Long price;

	@Column(nullable = false)
	private Long stockCount;

	@Column(nullable = false)
	private String imageUrl;

	public Menu(Store store, MenuCategory menuCategory, String name,
				Long price, Long stockCount, String imageUrl
	) {
		MenuValidator.validate(store, menuCategory, name, price, stockCount, imageUrl);
		this.store = store;
		this.menuCategory = menuCategory;
		this.name = name;
		this.price = price;
		this.stockCount = stockCount;
		this.imageUrl = imageUrl;
	}

	public Long getId() {
		return id;
	}

	public void decrementStockCount(int amount) {
		if (stockCount < amount) {
			throw new NotEnoughStockException("메뉴(id=" + id + "의 재고가 부족합니다.");
		}
		stockCount -= amount;
	}

	public long updatePrice(long uPrice) {
		if (uPrice <= 0) {
			throw new InvalidMenuPriceUpdateException("메뉴의 가격은 0원보다 커야합니다. 입력값 : " + uPrice);
		}
		this.price = uPrice;

		return this.price;
	}

	public Long getMenuCategoryId() {
		return menuCategory.getId();
	}

	public String getMenuCategoryName() {
		return menuCategory.getName();
	}

}
