package camp.woowak.lab.cart.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.exception.MenuNotFoundException;
import camp.woowak.lab.cart.exception.OtherStoreMenuException;
import camp.woowak.lab.cart.exception.StoreNotOpenException;
import camp.woowak.lab.cart.repository.CartRepository;
import camp.woowak.lab.cart.service.command.AddCartCommand;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;
import jakarta.transaction.Transactional;

@DisplayName("CartService 클래스")
@SpringBootTest
@Transactional
class CartServiceTest {
	@Autowired
	private PayAccountRepository payAccountRepository;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private StoreCategoryRepository storeCategoryRepository;
	@Autowired
	private StoreRepository storeRepository;
	@Autowired
	private VendorRepository vendorRepository;
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CartService cartService;

	private static final int minOrderPrice = 8000;
	private static final LocalDateTime startTime = LocalDateTime.now().minusMinutes(10).withSecond(0).withNano(0);
	private static final LocalDateTime endTime = LocalDateTime.now().plusMinutes(10).withSecond(0).withNano(0);

	private Customer customer;
	private Menu menu;
	private Store store;
	private Vendor vendor;

	@BeforeEach
	void setUp() {
		customer = createCustomer();

		vendor = createVendor();

		store = createStore(vendor, "중화반점", minOrderPrice, startTime, endTime);

		menu = createMenu(store, "짜장면", 90000);
	}

	@Nested
	@DisplayName("addMenu 메서드")
	class AddMenu {
		@Test
		@DisplayName("존재하는 메뉴를 장바구니에 추가할 수 있다.")
		void addMenuInCartWhenMenuExists() {
			//given
			AddCartCommand command = new AddCartCommand(customer.getId(), menu.getId());

			//when
			cartService.addMenu(command);

			//then
			Optional<Cart> cart = cartRepository.findByCustomerId(customer.getId());
			assertThat(cart).isPresent();
			Cart cartList = cart.get();
			assertThat(cartList.getMenuList()).contains(menu);
		}

		@Test
		@DisplayName("존재하지 않는 메뉴를 장바구니에 추가하면 MenuNotFoundException을 던진다.")
		void addMenuInCartWhenMenuDoesNotExist() {
			//given
			Long notExistsMenuId = Long.MAX_VALUE;
			AddCartCommand command = new AddCartCommand(customer.getId(), notExistsMenuId);

			//when & then
			assertThatThrownBy(() -> cartService.addMenu(command))
				.isExactlyInstanceOf(MenuNotFoundException.class);
		}

		@Test
		@DisplayName("가게가 열려있지 않다면 장바구니에 물건을 담을 수 없다.")
		void throwExceptionWhenAddMenuInCartWithClosedStoresMenu() {
			//given
			LocalDateTime startTime = LocalDateTime.now().minusMinutes(10).withSecond(0).withNano(0);
			LocalDateTime endTime = LocalDateTime.now().minusMinutes(1).withSecond(0).withNano(0);
			Store closedStore = createStore(vendor, "오픈 전의 중화반점", 8000, startTime, endTime);
			Menu menu = createMenu(closedStore, "오픈시간이 아니라 담을 수 없는 메뉴", 1000);

			AddCartCommand command = new AddCartCommand(customer.getId(), menu.getId());

			//when & then
			assertThatThrownBy(() -> cartService.addMenu(command))
				.isExactlyInstanceOf(StoreNotOpenException.class);
		}

		@Test
		@DisplayName("다른 가게의 메뉴를 함께 담을 수 없다.")
		void throwExceptionWhenAddMenuInCartWithOtherStoresMenu() {
			//given
			Store otherStore = createStore(vendor, "다른집", 8000, startTime, endTime);
			Menu otherStoresMenu = createMenu(otherStore, "다른집 짜장면", 9000);

			AddCartCommand command1 = new AddCartCommand(customer.getId(), menu.getId());
			cartService.addMenu(command1);

			//when & then
			AddCartCommand otherStoreCommand = new AddCartCommand(customer.getId(), otherStoresMenu.getId());
			assertThatThrownBy(() -> cartService.addMenu(otherStoreCommand))
				.isExactlyInstanceOf(OtherStoreMenuException.class);
		}
	}

	private Menu createMenu(Store store, String name, int price) {
		Menu menu1 = new Menu(store, name, price);
		menuRepository.saveAndFlush(menu1);

		return menu1;
	}

	private Store createStore(Vendor vendor, String name, int minOrderPrice, LocalDateTime startTime,
							  LocalDateTime endTime) {
		StoreCategory storeCategory = new StoreCategory("중국집");
		storeCategoryRepository.saveAndFlush(storeCategory);

		Store store1 = new Store(vendor, storeCategory, name, "송파", "010-1111-2222", minOrderPrice, startTime,
			endTime);
		storeRepository.saveAndFlush(store1);

		return store1;
	}

	private Customer createCustomer() {
		PayAccount payAccount = new PayAccount();
		payAccountRepository.save(payAccount);
		PasswordEncoder passwordEncoder = new NoOpPasswordEncoder();
		Customer customer1 = new Customer("customerName", "customer@example.com", "customerPassword", "010-1234-5647",
			payAccount, passwordEncoder);
		customerRepository.saveAndFlush(customer1);

		return customer1;
	}

	private Vendor createVendor() {
		PayAccount payAccount = new PayAccount();
		payAccountRepository.saveAndFlush(payAccount);
		PasswordEncoder passwordEncoder = new NoOpPasswordEncoder();
		Vendor vendor1 = new Vendor("vendorName", "vendorEmail@example.com", "vendorPassword", "010-0000-0000",
			payAccount,
			passwordEncoder);
		vendorRepository.saveAndFlush(vendor1);

		return vendor1;
	}
}