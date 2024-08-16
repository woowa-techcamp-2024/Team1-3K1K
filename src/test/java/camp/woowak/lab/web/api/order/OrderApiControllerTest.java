package camp.woowak.lab.web.api.order;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import camp.woowak.lab.order.service.RetrieveOrderListService;
import camp.woowak.lab.order.service.command.RetrieveOrderListCommand;
import camp.woowak.lab.store.exception.NotEqualsOwnerException;
import camp.woowak.lab.store.exception.NotFoundStoreException;
import camp.woowak.lab.web.authentication.LoginVendor;
import camp.woowak.lab.web.resolver.session.SessionConst;

@WebMvcTest(OrderApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class OrderApiControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RetrieveOrderListService retrieveOrderListService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("점주 주문 리스트 조회 테스트 - 성공")
	void testRetrieveOrderList() throws Exception {
		// given
		given(retrieveOrderListService.retrieveOrderListOfVendorStores(any())).willReturn(new ArrayList<>());
		MockHttpSession session = new MockHttpSession();
		LoginVendor loginVendor = new LoginVendor(UUID.randomUUID());
		session.setAttribute(SessionConst.SESSION_VENDOR_KEY, loginVendor);

		// when
		ResultActions ra = mockMvc.perform(get("/orders")
			.session(session));

		// then
		ra.andExpect(status().isOk());
	}

	@Test
	@DisplayName("점주 매장 주문 리스트 조회 테스트 - 성공")
	void testRetrieveOrderListByStore() throws Exception {
		// given
		given(retrieveOrderListService.retrieveOrderListOfStore(any(RetrieveOrderListCommand.class))).willReturn(
			new ArrayList<>());
		MockHttpSession session = new MockHttpSession();
		LoginVendor loginVendor = new LoginVendor(UUID.randomUUID());
		session.setAttribute(SessionConst.SESSION_VENDOR_KEY, loginVendor);

		// when
		ResultActions ra = mockMvc.perform(get("/orders/stores/1")
			.contentType("application/json")
			.session(session));

		// then
		ra.andExpect(status().isOk());
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
