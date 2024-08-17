package camp.woowak.lab.web.api.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import camp.woowak.lab.order.exception.EmptyCartException;
import camp.woowak.lab.order.exception.MinimumOrderPriceNotMetException;
import camp.woowak.lab.order.exception.MultiStoreOrderException;
import camp.woowak.lab.order.service.OrderCreationService;
import camp.woowak.lab.order.service.command.OrderCreationCommand;
import camp.woowak.lab.payaccount.exception.InsufficientBalanceException;
import camp.woowak.lab.web.authentication.LoginCustomer;
import camp.woowak.lab.web.resolver.session.SessionConst;

@WebMvcTest(controllers = OrderApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class OrderApiControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private OrderCreationService orderCreationService;

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
}
