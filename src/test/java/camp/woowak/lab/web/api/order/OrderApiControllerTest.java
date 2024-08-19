package camp.woowak.lab.web.api.order;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import camp.woowak.lab.fixture.CustomerFixture;
import camp.woowak.lab.fixture.StoreFixture;
import camp.woowak.lab.fixture.VendorFixture;
import camp.woowak.lab.order.domain.vo.OrderItem;
import camp.woowak.lab.order.exception.EmptyCartException;
import camp.woowak.lab.order.exception.MinimumOrderPriceNotMetException;
import camp.woowak.lab.order.exception.MultiStoreOrderException;
import camp.woowak.lab.order.service.OrderCreationService;
import camp.woowak.lab.order.service.RetrieveOrderListService;
import camp.woowak.lab.order.service.command.OrderCreationCommand;
import camp.woowak.lab.order.service.command.RetrieveOrderListCommand;
import camp.woowak.lab.order.service.dto.OrderDTO;
import camp.woowak.lab.order.service.dto.TestOrderDTO;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.exception.InsufficientBalanceException;
import camp.woowak.lab.store.exception.NotEqualsOwnerException;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import camp.woowak.lab.web.authentication.LoginCustomer;
import camp.woowak.lab.web.authentication.LoginVendor;
import camp.woowak.lab.web.resolver.session.SessionConst;

@WebMvcTest(controllers = OrderApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class OrderApiControllerTest implements CustomerFixture, VendorFixture, StoreFixture {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RetrieveOrderListService retrieveOrderListService;

	@MockBean
	private OrderCreationService orderCreationService;

	@Override
	public PayAccount createPayAccount() {
		return CustomerFixture.super.createPayAccount();
	}

	@Nested
	@DisplayName("판매자 회원가입: POST /vendors")
	class SignUpVendor {
		@Test
		@DisplayName("[성공] 201")
		void success() throws Exception {
			LoginCustomer loginCustomer = new LoginCustomer(UUID.randomUUID());
			Long fakeOrderId = 1L;
			BDDMockito.given(orderCreationService.create(BDDMockito.any(OrderCreationCommand.class)))
				.willReturn(fakeOrderId);

			// when
			ResultActions actions = mockMvc.perform(
				post("/orders")
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.sessionAttr(SessionConst.SESSION_CUSTOMER_KEY, loginCustomer)
			);

			// then
			actions.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
				.andExpect(jsonPath("$.data.orderId").value(fakeOrderId))
				.andDo(print());
		}

		@Nested
		@DisplayName("[실패] 400")
		class FailWith400 {
			@Nested
			@DisplayName("카트가")
			class CartMust {
				@Test
				@DisplayName("비어있는 경우")
				void failWithEmptyCart() throws Exception {
					LoginCustomer loginCustomer = new LoginCustomer(UUID.randomUUID());
					BDDMockito.given(orderCreationService.create(BDDMockito.any(OrderCreationCommand.class)))
						.willThrow(new EmptyCartException("최소 하나 이상의 메뉴를 주문해야 합니다."));

					// when
					ResultActions actions = mockMvc.perform(
						post("/orders")
							.accept(MediaType.APPLICATION_JSON)
							.contentType(MediaType.APPLICATION_JSON)
							.sessionAttr(SessionConst.SESSION_CUSTOMER_KEY, loginCustomer)
					);

					// then
					actions.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.type").value("about:blank"))
						.andExpect(jsonPath("$.title").value("Bad Request"))
						.andExpect(jsonPath("$.status").value(400))
						.andExpect(jsonPath("$.instance").value("/orders"))
						.andDo(print());
				}
			}

			@Nested
			@DisplayName("가게는")
			class StoreMust {
				@Test
				@DisplayName("단일 가게에 대한 주문만 가능하다.")
				void failWithEmptyCart() throws Exception {
					LoginCustomer loginCustomer = new LoginCustomer(UUID.randomUUID());
					BDDMockito.given(orderCreationService.create(BDDMockito.any(OrderCreationCommand.class)))
						.willThrow(new MultiStoreOrderException("다른 가게의 메뉴를 같이 주문할 수 없습니다."));

					// when
					ResultActions actions = mockMvc.perform(
						post("/orders")
							.accept(MediaType.APPLICATION_JSON)
							.contentType(MediaType.APPLICATION_JSON)
							.sessionAttr(SessionConst.SESSION_CUSTOMER_KEY, loginCustomer)
					);

					// then
					actions.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.type").value("about:blank"))
						.andExpect(jsonPath("$.title").value("Bad Request"))
						.andExpect(jsonPath("$.status").value(400))
						.andExpect(jsonPath("$.instance").value("/orders"))
						.andDo(print());
				}
			}

			@Nested
			@DisplayName("주문 금액은")
			class OrderPriceMust {
				@Test
				@DisplayName("가게의 최소 주문금액보다 커야한다.")
				void failWithEmptyCart() throws Exception {
					LoginCustomer loginCustomer = new LoginCustomer(UUID.randomUUID());
					BDDMockito.given(orderCreationService.create(BDDMockito.any(OrderCreationCommand.class)))
						.willThrow(new MinimumOrderPriceNotMetException("주문 금액이 최소 주문금액보다 적습니다."));

					// when
					ResultActions actions = mockMvc.perform(
						post("/orders")
							.accept(MediaType.APPLICATION_JSON)
							.contentType(MediaType.APPLICATION_JSON)
							.sessionAttr(SessionConst.SESSION_CUSTOMER_KEY, loginCustomer)
					);

					// then
					actions.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.type").value("about:blank"))
						.andExpect(jsonPath("$.title").value("Bad Request"))
						.andExpect(jsonPath("$.status").value(400))
						.andExpect(jsonPath("$.instance").value("/orders"))
						.andDo(print());
				}
			}

			@Nested
			@DisplayName("계좌의 잔액은")
			class PayAccountMust {
				@Test
				@DisplayName("주문 금액보다 커야한다.")
				void failWithEmptyCart() throws Exception {
					LoginCustomer loginCustomer = new LoginCustomer(UUID.randomUUID());
					BDDMockito.given(orderCreationService.create(BDDMockito.any(OrderCreationCommand.class)))
						.willThrow(new InsufficientBalanceException("잔액이 부족합니다."));

					// when
					ResultActions actions = mockMvc.perform(
						post("/orders")
							.accept(MediaType.APPLICATION_JSON)
							.contentType(MediaType.APPLICATION_JSON)
							.sessionAttr(SessionConst.SESSION_CUSTOMER_KEY, loginCustomer)
					);

					// then
					actions.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.type").value("about:blank"))
						.andExpect(jsonPath("$.title").value("Bad Request"))
						.andExpect(jsonPath("$.status").value(400))
						.andExpect(jsonPath("$.instance").value("/orders"))
						.andDo(print());
				}
			}
		}
	}

	@Test
	@DisplayName("점주 주문 리스트 조회 테스트 - 성공")
	void testRetrieveOrderList() throws Exception {
		// given
		List<OrderItem> orderItems = List.of(new OrderItem(1L, 1000, 2), new OrderItem(2L, 1000, 2),
			new OrderItem(3L, 1000, 2));
		List<OrderDTO> orders = List.of(
			new TestOrderDTO(1L, createCustomer(UUID.randomUUID()), createTestStore(1L, createTestVendor()),
				orderItems),
			new TestOrderDTO(1L, createCustomer(UUID.randomUUID()), createTestStore(2L, createTestVendor()),
				orderItems));
		given(retrieveOrderListService.retrieveOrderListOfVendorStores(any())).willReturn(new PageImpl<>(orders));
		MockHttpSession session = new MockHttpSession();
		LoginVendor loginVendor = new LoginVendor(UUID.randomUUID());
		session.setAttribute(SessionConst.SESSION_VENDOR_KEY, loginVendor);

		// when
		ResultActions ra = mockMvc.perform(get("/orders")
			.session(session));

		// then
		ra.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.orders").isArray())
			.andExpect(jsonPath("$.data.orders.length()").value(orders.size()))// 주문의 개수 확인
		;
	}

	@Test
	@DisplayName("점주 매장 주문 리스트 조회 테스트 - 성공")
	void testRetrieveOrderListByStore() throws Exception {
		// given
		List<OrderItem> orderItems = List.of(new OrderItem(1L, 1000, 2), new OrderItem(2L, 1000, 2),
			new OrderItem(3L, 1000, 2));
		List<OrderDTO> orders = List.of(
			new TestOrderDTO(1L, createCustomer(UUID.randomUUID()), createTestStore(1L, createTestVendor()),
				orderItems),
			new TestOrderDTO(2L, createCustomer(UUID.randomUUID()), createTestStore(1L, createTestVendor()),
				orderItems));
		given(retrieveOrderListService.retrieveOrderListOfStore(any(RetrieveOrderListCommand.class))).willReturn(
			new PageImpl<>(orders));
		MockHttpSession session = new MockHttpSession();
		LoginVendor loginVendor = new LoginVendor(UUID.randomUUID());
		session.setAttribute(SessionConst.SESSION_VENDOR_KEY, loginVendor);

		// when
		ResultActions ra = mockMvc.perform(get("/orders/stores/1")
			.contentType("application/json")
			.session(session));

		// then
		ra.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.orders").isArray());
		for (int orderIndex = 0; orderIndex < orders.size(); orderIndex++) {
			ra.andExpect(jsonPath("$.data.orders[" + orderIndex + "].id").value(orders.get(orderIndex).getId()))
				.andExpect(jsonPath("$.data.orders[" + orderIndex + "].orderItems").isArray());
			for (int orderItemIndex = 0; orderItemIndex < orderItems.size(); orderItemIndex++) {
				ra.andExpect(
						jsonPath("$.data.orders[" + orderIndex + "].orderItems[" + orderItemIndex + "].menuId").value(
							orderItems.get(orderItemIndex).getMenuId()))
					.andExpect(
						jsonPath("$.data.orders[" + orderIndex + "].orderItems[" + orderItemIndex + "].price").value(
							orderItems.get(orderItemIndex).getPrice()))
					.andExpect(
						jsonPath("$.data.orders[" + orderIndex + "].orderItems[" + orderItemIndex + "].quantity").value(
							orderItems.get(orderItemIndex).getQuantity()))
					.andExpect(
						jsonPath(
							"$.data.orders[" + orderIndex + "].orderItems[" + orderItemIndex + "].totalPrice").value(
							orderItems.get(orderItemIndex).getTotalPrice()));
			}
		}
	}

	@Test
	@DisplayName("점주 매장 주문 리스트 조회 테스트 - 실패(매장이 존재하지 않음)")
	void testRetrieveOrderListByStoreFail() throws Exception {
		// given
		given(retrieveOrderListService.retrieveOrderListOfStore(any(RetrieveOrderListCommand.class))).willThrow(
			new NotFoundStoreException("해당 매장이 존재하지 않습니다."));
		MockHttpSession session = new MockHttpSession();
		LoginVendor loginVendor = new LoginVendor(UUID.randomUUID());
		session.setAttribute(SessionConst.SESSION_VENDOR_KEY, loginVendor);

		// when
		ResultActions ra = mockMvc.perform(get("/orders/stores/1")
			.contentType("application/json")
			.session(session));

		// then
		ra.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("점주 매장 주문 리스트 조회 테스트 - 실패(매장의 주인이 아님)")
	void testRetrieveOrderListByStoreFailWithUnauthorized() throws Exception {
		// given
		given(retrieveOrderListService.retrieveOrderListOfStore(any(RetrieveOrderListCommand.class))).willThrow(
			new NotEqualsOwnerException("해당 매장의 주인이 아닙니다."));
		MockHttpSession session = new MockHttpSession();
		LoginVendor loginVendor = new LoginVendor(UUID.randomUUID());
		session.setAttribute(SessionConst.SESSION_VENDOR_KEY, loginVendor);

		// when
		ResultActions ra = mockMvc.perform(get("/orders/stores/1")
			.contentType("application/json")
			.session(session));

		// then
		ra.andExpect(status().isUnauthorized());
	}
}
