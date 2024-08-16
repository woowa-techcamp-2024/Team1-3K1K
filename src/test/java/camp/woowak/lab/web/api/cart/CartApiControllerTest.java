package camp.woowak.lab.web.api.cart;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.exception.CartErrorCode;
import camp.woowak.lab.cart.repository.CartRepository;
import camp.woowak.lab.cart.service.command.CartTotalPriceCommand;
import camp.woowak.lab.common.exception.ErrorCode;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.menu.repository.MenuCategoryRepository;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.LoginCustomer;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;
import camp.woowak.lab.web.dto.request.cart.AddCartRequest;
import camp.woowak.lab.web.resolver.session.SessionConst;
import jakarta.transaction.Transactional;

@DisplayName("CartApiController 클래스")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class CartApiControllerTest {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private PayAccountRepository payAccountRepository;
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private MenuCategoryRepository menuCategoryRepository;
	@Autowired
	private StoreCategoryRepository storeCategoryRepository;
	@Autowired
	private StoreRepository storeRepository;
	@Autowired
	private VendorRepository vendorRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private CartRepository cartRepository;

	private MockHttpSession session;
	private Customer customer;
	private Vendor vendor;
	private Store store;
	private MenuCategory menuCategory;
	private static final LocalDateTime startTime = LocalDateTime.now().minusMinutes(10).withSecond(0).withNano(0);
	private static final LocalDateTime endTime = LocalDateTime.now().plusMinutes(10).withSecond(0).withNano(0);

	@BeforeEach
	void setUp() throws Exception {
		customer = createCustomer();
		vendor = createVendor();
		store = createStore(vendor, "중화반점", 8000, startTime, endTime);
		menuCategory = createMenuCategory(store, "카테고리");

		session = new MockHttpSession();
		session.setAttribute(SessionConst.SESSION_CUSTOMER_KEY, new LoginCustomer(customer.getId()));
	}

	@Nested
	@DisplayName("addMenu 메서드는")
	class AddMenu {
		private final String BASE_URL = "/cart";
		private Menu menu;
		private static final int minOrderPrice = 8000;

		@BeforeEach
		void setUp() throws Exception {
			menu = createMenu(store, menuCategory, "짜장면", 90000);
		}

		@Test
		@DisplayName("존재하는 메뉴를 장바구니에 담을 수 있다.")
		void addMenuWithExistsMenu() throws Exception {
			//given
			AddCartRequest request = new AddCartRequest(menu.getId());
			String content = mapper.writeValueAsString(request);

			//when & then
			mvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(content).session(session))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.success").value(true));
		}

		@Test
		@DisplayName("존재하지 않는 메뉴는 장바구니에 담을 수 없다.")
		void cantAddMenuWithNonexistentMenu() throws Exception {
			//given
			AddCartRequest request = new AddCartRequest(Long.MAX_VALUE);
			String content = mapper.writeValueAsString(request);

			//when & then
			ResultActions actions = mvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(content)
					.session(session))
				.andExpect(status().isNotFound());

			validateErrorResponseWithErrorCode(actions, CartErrorCode.MENU_NOT_FOUND);
		}

		@Test
		@DisplayName("열리지 않은 가게의 메뉴를 장바구니에 담을 수 없다.")
		void cantAddMenuWithClosedStoresMenu() throws Exception {
			//given
			LocalDateTime closedStartTime = LocalDateTime.now().minusMinutes(10).withSecond(0).withNano(0);
			LocalDateTime closedEndTime = LocalDateTime.now().minusMinutes(1).withSecond(0).withNano(0);
			Store closedStore = createStore(vendor, "닫힌 가게", minOrderPrice, closedStartTime, closedEndTime);
			MenuCategory closedStoreMenuCategory = createMenuCategory(closedStore, "닫힌 카테고리");
			Menu closedStoresMenu = createMenu(closedStore, closedStoreMenuCategory, "닫힌 가게의 메뉴", 1000);

			AddCartRequest request = new AddCartRequest(closedStoresMenu.getId());
			String content = mapper.writeValueAsString(request);

			//when & then
			ResultActions action = mvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(content)
					.session(session))
				.andExpect(status().isBadRequest());

			validateErrorResponseWithErrorCode(action, CartErrorCode.STORE_NOT_OPEN);
		}

		@Test
		@DisplayName("서로 다른 가게의 메뉴를 장바구니에 담을 수 없다.")
		void cantAddMenuWithOtherStoresMenu() throws Exception {
			//given
			Store otherStore = createStore(vendor, "옆집 가게", minOrderPrice, startTime, endTime);
			MenuCategory otherStoreMenuCategory = createMenuCategory(otherStore, "옆집 카테고리");
			Menu otherStoreMenu = createMenu(otherStore, otherStoreMenuCategory, "옆집 가게 메뉴", 10000);

			AddCartRequest givenRequest = new AddCartRequest(menu.getId());
			String givenContent = mapper.writeValueAsString(givenRequest);

			AddCartRequest request = new AddCartRequest(otherStoreMenu.getId());
			String content = mapper.writeValueAsString(request);

			//기존에 있던 가게의 메뉴를 담아둠
			mvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(givenContent)
				.session(session));

			//when & then
			ResultActions actions = mvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(content)
					.session(session))
				.andExpect(status().isBadRequest());

			validateErrorResponseWithErrorCode(actions, CartErrorCode.OTHER_STORE_MENU);
		}
	}

	@Nested
	@DisplayName("getCartTotalPrice 메서드는")
	class GetCartTotalPrice {
		private final String BASE_URL = "/cart/price";

		@Test
		@DisplayName("장바구니가 비어있으면 0원을 return한다.")
		void getTotalPriceWithEmptyList() throws Exception {
			//given

			//when & then
			mvc.perform(get(BASE_URL)
					.session(session))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.totalPrice").value(0));
		}

		@Test
		@DisplayName("현재 장바구니에 담긴 모든 메뉴의 총 금액을 return 받는다.")
		void getTotalPriceTest() throws Exception {
			//given
			Cart cart = new Cart(customer.getId().toString());

			int price1 = 1000;
			Menu menu1 = createMenu(store, menuCategory, "짜장면1", price1);
			cart.addMenu(menu1);

			int price2 = 2000;
			Menu menu2 = createMenu(store, menuCategory, "짬뽕1", price2);
			cart.addMenu(menu2);

			int price3 = Integer.MAX_VALUE;
			Menu menu3 = createMenu(store, menuCategory, "황제정식", price3);
			cart.addMenu(menu3);
			cartRepository.save(cart);

			CartTotalPriceCommand command = new CartTotalPriceCommand(customer.getId().toString());

			//when & then
			mvc.perform(get(BASE_URL)
					.session(session))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.totalPrice").value(
					(long)menu1.getPrice() + (long)menu2.getPrice() + (long)menu3.getPrice()));
		}
	}

	private MenuCategory createMenuCategory(Store store, String name) {
		MenuCategory menuCategory = new MenuCategory(store, name);
		return menuCategoryRepository.saveAndFlush(menuCategory);
	}

	private Menu createMenu(Store store, MenuCategory menuCategory, String name, int price) {
		Menu menu1 = new Menu(store, menuCategory, name, price, 1L, "imageUrl");
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

	private ResultActions validateErrorResponseWithErrorCode(ResultActions actions, ErrorCode errorCode) throws
		Exception {
		return actions.andExpect(jsonPath("$.status").value(errorCode.getStatus()))
			.andExpect(jsonPath("$.detail").value(errorCode.getMessage()))
			.andExpect(jsonPath("$.errorCode").value(errorCode.getErrorCode()));
	}
}