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

	private List<Menu> cartList;
	private Cart cart;
	private Menu menu;
	private int minPrice = 8000;
	private Store store;
	private Vendor vendor;

	@BeforeEach
	void setUp() {
		cartList = new LinkedList<>();
		cart = new Cart(customerId, cartList);

		vendor = createSavedVendor(UUID.randomUUID(), new PayAccount(), new NoOpPasswordEncoder());
		store = createSavedStore(1L, vendor, "중화반점", minPrice,
			LocalDateTime.now().minusMinutes(10).withSecond(0).withNano(0),
			LocalDateTime.now().plusMinutes(10).withSecond(0).withNano(0));
		menu = createSavedMenu(1L, store, new MenuCategory(store, "중식"), "짜장면", 9000);
	}

	@Nested
	@DisplayName("addMenu 메서드")
	class AddMenuTest {
		@Test
		@DisplayName("Menu를 받으면 cart에 저장된다.")
		void addMenuTest() {
			//given

			//when
			cart.addMenu(menu);

			//then
			assertThat(cartList.get(0)).isEqualTo(menu);
		}

		@Test
		@DisplayName("열지 않은 가게의 메뉴를 담으면 StoreNotOpenException을 던진다.")
		void storeNotOpenExceptionTest() {
			//given
			Store closedStore = createSavedStore(2L, vendor, "closed", minPrice,
				LocalDateTime.now().minusMinutes(30).withSecond(0).withNano(0),
				LocalDateTime.now().minusMinutes(10).withSecond(0).withNano(0));
			Menu closedMenu = createSavedMenu(2L, closedStore, new MenuCategory(closedStore, "중식"), "짬뽕", 9000);

			//when & then
			assertThatThrownBy(() -> cart.addMenu(closedMenu))
				.isExactlyInstanceOf(StoreNotOpenException.class);
			assertThat(cartList).isEmpty();
		}

		@Test
		@DisplayName("다른 가게의 메뉴를 함께 담으면 OtherStoreMenuException을 던진다.")
		void otherStoreMenuExceptionTest() {
			//given
			cart.addMenu(menu);

			Store otherStore = createSavedStore(2L, vendor, "closed", minPrice,
				LocalDateTime.now().minusMinutes(30).withSecond(0).withNano(0),
				LocalDateTime.now().plusMinutes(30).withSecond(0).withNano(0));
			Menu otherStoreMenu = createSavedMenu(2L, otherStore, new MenuCategory(otherStore, "중식"), "짬뽕", 9000);

			//when & then
			assertThatThrownBy(() -> cart.addMenu(otherStoreMenu))
				.isExactlyInstanceOf(OtherStoreMenuException.class);
			assertThat(cartList).doesNotContain(otherStoreMenu);
			assertThat(cartList).contains(menu);
		}
	}

	@Nested
	@DisplayName("totalPrice 메서드는")
	class GetTotalPriceTest {
		@Test
		@DisplayName("장바구니가 비어있으면 0원을 return한다.")
		void getTotalPriceWithEmptyList() {
			//given

			//when
			long totalPrice = cart.getTotalPrice();

			//then
			assertThat(totalPrice).isEqualTo(0);
		}

		@Test
		@DisplayName("현재 장바구니에 담긴 모든 메뉴의 총 금액을 return 받는다.")
		void getTotalPriceTest() {
			//given
			MenuCategory menuCategory = new MenuCategory(store, "중식");
			int price1 = 1000;
			Menu menu1 = createSavedMenu(2L, store, menuCategory, "짜장면1", price1);
			cart.addMenu(menu1);

			int price2 = 2000;
			Menu menu2 = createSavedMenu(3L, store, menuCategory, "짬뽕1", price2);
			cart.addMenu(menu2);

			int price3 = Integer.MAX_VALUE;
			Menu menu3 = createSavedMenu(4L, store, menuCategory, "황제정식", price3);
			cart.addMenu(menu3);

			//when
			long totalPrice = cart.getTotalPrice();

			//then
			assertThat(totalPrice).isEqualTo((long)price1 + (long)price2 + (long)price3);
		}
	}
}