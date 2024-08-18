package camp.woowak.lab.cart.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.cart.domain.vo.CartItem;
import camp.woowak.lab.cart.exception.OtherStoreMenuException;
import camp.woowak.lab.cart.exception.StoreNotOpenException;
import camp.woowak.lab.fixture.CartFixture;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;

@DisplayName("Cart 클래스는")
class CartTest implements CartFixture {
	private final String customerId = UUID.randomUUID().toString();

	private List<CartItem> cartItemList;
	private Cart cart;
	private Menu menu;
	private final int minPrice = 8000;
	private Store store;
	private Vendor vendor;

	@BeforeEach
	void setUp() {
		cartItemList = new LinkedList<>();
		cart = new Cart(customerId, cartItemList);

		vendor = createSavedVendor(UUID.randomUUID(), new PayAccount(), new NoOpPasswordEncoder());
		store = createSavedStore(1L, vendor, "중화반점", minPrice,
			LocalDateTime.now().minusMinutes(10).withSecond(0).withNano(0),
			LocalDateTime.now().plusMinutes(10).withSecond(0).withNano(0));
		menu = createSavedMenu(1L, store, new MenuCategory(store, "중식"), "짜장면", 9000L);
	}

	@Nested
	@DisplayName("addMenu 메서드는")
	class AddMenuTest {
		@Test
		@DisplayName("Menu를 받으면 cart에 저장된다.")
		void addMenuTest() {
			//given

			//when
			cart.addMenu(menu);

			//then
			assertThat(cartItemList).hasSize(1);
			assertThat(cartItemList.get(0).getMenuId()).isEqualTo(menu.getId());
			assertThat(cartItemList.get(0).getAmount()).isEqualTo(1);
		}

		@Test
		@DisplayName("열지 않은 가게의 메뉴를 담으면 StoreNotOpenException을 던진다.")
		void storeNotOpenExceptionTest() {
			//given
			Store closedStore = createSavedStore(2L, vendor, "closed", minPrice,
				LocalDateTime.now().minusMinutes(30).withSecond(0).withNano(0),
				LocalDateTime.now().minusMinutes(10).withSecond(0).withNano(0));
			Menu closedMenu = createSavedMenu(2L, closedStore, new MenuCategory(closedStore, "중식"), "짬뽕", 9000L);

			//when & then
			assertThatThrownBy(() -> cart.addMenu(closedMenu))
				.isExactlyInstanceOf(StoreNotOpenException.class);
			assertThat(cartItemList).isEmpty();
		}

		@Test
		@DisplayName("다른 가게의 메뉴를 함께 담으면 OtherStoreMenuException을 던진다.")
		void otherStoreMenuExceptionTest() {
			//given
			cart.addMenu(menu);

			Store otherStore = createSavedStore(2L, vendor, "otherStore", minPrice,
				LocalDateTime.now().minusMinutes(30).withSecond(0).withNano(0),
				LocalDateTime.now().plusMinutes(30).withSecond(0).withNano(0));
			Menu otherStoreMenu = createSavedMenu(2L, otherStore, new MenuCategory(otherStore, "중식"), "짬뽕", 9000L);

			//when & then
			assertThatThrownBy(() -> cart.addMenu(otherStoreMenu))
				.isExactlyInstanceOf(OtherStoreMenuException.class);

			//then
			assertThat(cartItemList).hasSize(1);
			assertThat(cartItemList)
				.containsExactly(new CartItem(menu.getId(), store.getId(), 1))
				.doesNotContain(new CartItem(otherStoreMenu.getId(), otherStore.getId(), 1));
		}
	}
}
