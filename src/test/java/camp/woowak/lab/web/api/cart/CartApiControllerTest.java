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

import camp.woowak.lab.cart.exception.CartErrorCode;
import camp.woowak.lab.common.exception.ErrorCode;
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
	private StoreCategoryRepository storeCategoryRepository;
	@Autowired
	private StoreRepository storeRepository;
	@Autowired
	private VendorRepository vendorRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private ObjectMapper mapper;

	@Nested
	@DisplayName("addMenu 메서드는")
	class AddMenu {
		private final String BASE_URL = "/cart";
		private Customer customer;
		private Vendor vendor;
		private Menu menu;
		private static final int minOrderPrice = 8000;
		private static final LocalDateTime startTime = LocalDateTime.now().minusMinutes(10).withSecond(0).withNano(0);
		private static final LocalDateTime endTime = LocalDateTime.now().plusMinutes(10).withSecond(0).withNano(0);
		private MockHttpSession session;

		@BeforeEach
		void setUp() throws Exception {
			customer = createCustomer();
			vendor = createVendor();
			Store store = createStore(vendor, "중화반점", 8000, startTime, endTime);
			menu = createMenu(store, "짜장면", 90000, 10);
			session = new MockHttpSession();
			session.setAttribute(SessionConst.SESSION_CUSTOMER_KEY, new LoginCustomer(customer.getId()));
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
			Menu closedStoresMenu = createMenu(closedStore, "닫힌 가게의 메뉴", 1000, 10);

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
			Menu otherStoreMenu = createMenu(otherStore, "옆집 가게 메뉴", 10000, 10);

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

	private Menu createMenu(Store store, String name, int price, int quantity) {
		Menu menu1 = new Menu(store, name, price, quantity);
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