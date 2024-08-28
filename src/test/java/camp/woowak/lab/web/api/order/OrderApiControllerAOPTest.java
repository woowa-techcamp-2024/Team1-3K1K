package camp.woowak.lab.web.api.order;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import camp.woowak.lab.order.service.OrderCreationService;
import camp.woowak.lab.order.service.RetrieveOrderListService;
import camp.woowak.lab.order.service.command.OrderCreationCommand;
import camp.woowak.lab.web.authentication.LoginCustomer;
import camp.woowak.lab.web.dto.response.order.OrderCreationResponse;
import camp.woowak.lab.web.resolver.session.SessionConst;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderApiControllerAOPTest {
	@InjectMocks
	private OrderApiController orderApiController;
	@MockBean
	private OrderCreationService orderCreationService;
	@MockBean
	private RetrieveOrderListService retrieveOrderListService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private MockMvc mvc;

	@Test
	@DisplayName("[성공] 요청을 처리하면 멱등성키-결과값을 redis에 저장함")
	void idempotentSuccess() throws Exception {
		//given
		String idempotentKey = UUID.randomUUID().toString();
		UUID customerId = UUID.randomUUID();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SessionConst.SESSION_CUSTOMER_KEY, new LoginCustomer(customerId));

		given(orderCreationService.create(any(OrderCreationCommand.class)))
			.willReturn(1L);

		//when
		ResultActions actions = mvc.perform(post("/orders")
												.session(session)
												.accept(MediaType.APPLICATION_JSON)
												.contentType(MediaType.APPLICATION_JSON)
												.header("Idempotency-Key", idempotentKey));

		//then
		actions.andExpect(status().isCreated());
		// assertThat();
		OrderCreationResponse response = (OrderCreationResponse)redisTemplate.opsForValue()
			.get("IDEMPOTENT_KEY: " + idempotentKey);
		assertThat(response.orderId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("[성공] 이전에 보낸 요청이 결과값을 저장했다면 서비스에 들어가지 않고 결과값을 반환")
	void idempotentSuccessWithBeforeResult() throws Exception {
		//given
		String idempotentKey = UUID.randomUUID().toString();
		UUID customerId = UUID.randomUUID();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SessionConst.SESSION_CUSTOMER_KEY, new LoginCustomer(customerId));

		given(orderCreationService.create(any(OrderCreationCommand.class)))
			.willReturn(1L);

		//when
		ResultActions action1 = mvc.perform(post("/orders").session(session)
												.accept(MediaType.APPLICATION_JSON)
												.contentType(MediaType.APPLICATION_JSON)
												.header("Idempotency-Key", idempotentKey));
		ResultActions action2 = mvc.perform(post("/orders").session(session)
												.accept(MediaType.APPLICATION_JSON)
												.contentType(MediaType.APPLICATION_JSON)
												.header("Idempotency-Key", idempotentKey));
		ResultActions action3 = mvc.perform(post("/orders").session(session)
												.accept(MediaType.APPLICATION_JSON)
												.contentType(MediaType.APPLICATION_JSON)
												.header("Idempotency-Key", idempotentKey));
		ResultActions action4 = mvc.perform(post("/orders").session(session)
												.accept(MediaType.APPLICATION_JSON)
												.contentType(MediaType.APPLICATION_JSON)
												.header("Idempotency-Key", idempotentKey));
		ResultActions action5 = mvc.perform(post("/orders").session(session)
												.accept(MediaType.APPLICATION_JSON)
												.contentType(MediaType.APPLICATION_JSON)
												.header("Idempotency-Key", idempotentKey));

		//then
		verify(orderCreationService, times(1)).create(any(OrderCreationCommand.class));
		action1.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.orderId").value(1L));
		action2.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.orderId").value(1L));
		action3.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.orderId").value(1L));
		action4.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.orderId").value(1L));
		action5.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.orderId").value(1L));
	}

	@Test
	@DisplayName("[실패] 멱등성키를 가져오지 않으면 오류 출력")
	void notExistsIdempotentFailure() throws Exception {
		//given
		UUID customerId = UUID.randomUUID();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SessionConst.SESSION_CUSTOMER_KEY, new LoginCustomer(customerId));

		//when
		ResultActions actions = mvc.perform(post("/orders").session(session));

		//then
		actions.andExpect(status().isUnauthorized());
	}

}
