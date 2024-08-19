package camp.woowak.lab.web.api.menu;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import camp.woowak.lab.menu.exception.NotEqualsOwnerException;
import camp.woowak.lab.menu.exception.NotUpdatableTimeException;
import camp.woowak.lab.menu.service.UpdateMenuStockService;
import camp.woowak.lab.menu.service.command.UpdateMenuStockCommand;
import camp.woowak.lab.web.authentication.LoginVendor;
import camp.woowak.lab.web.dto.request.menu.UpdateMenuStockRequest;
import camp.woowak.lab.web.resolver.session.SessionConst;

@WebMvcTest(MenuApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MenuApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UpdateMenuStockService updateMenuStockService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("메뉴 재고 업데이트 테스트 - 성공")
	void testUpdateMenuStock() throws Exception {
		// given
		Long menuId = 1L;
		int stock = 10;
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SessionConst.SESSION_VENDOR_KEY, new LoginVendor(UUID.randomUUID()));
		UpdateMenuStockRequest request = new UpdateMenuStockRequest(menuId, stock);

		given(updateMenuStockService.updateMenuStock(any(UpdateMenuStockCommand.class))).willReturn(menuId);

		// when
		mockMvc.perform(patch("/menus/stock")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.session(session))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.menuId").value(menuId));
	}

	@Test
	@DisplayName("메뉴 재고 업데이트 테스트 - 실패(stock이 0 미만)")
	void testUpdateMenuStockFailWithStockUnderZero() throws Exception {
		// given
		Long menuId = 1L;
		int stock = -1;
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SessionConst.SESSION_VENDOR_KEY, new LoginVendor(UUID.randomUUID()));
		UpdateMenuStockRequest request = new UpdateMenuStockRequest(menuId, stock);

		// when
		mockMvc.perform(patch("/menus/stock")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.session(session))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("메뉴 재고 업데이트 테스트 - 실패(메뉴id  null)")
	void testUpdateMenuStockFailWithMenuIdNull() throws Exception {
		// given
		Long menuId = null;
		int stock = 10;
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SessionConst.SESSION_VENDOR_KEY, new LoginVendor(UUID.randomUUID()));
		UpdateMenuStockRequest request = new UpdateMenuStockRequest(menuId, stock);

		// when
		mockMvc.perform(patch("/menus/stock")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.session(session))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("메뉴 재고 업데이트 테스트 - 실패(점주 불일치)")
	void testUpdateMenuStockFail() throws Exception {
		// given
		Long menuId = 1L;
		int stock = 10;
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SessionConst.SESSION_VENDOR_KEY, new LoginVendor(UUID.randomUUID()));
		UpdateMenuStockRequest request = new UpdateMenuStockRequest(menuId, stock);

		given(updateMenuStockService.updateMenuStock(any(UpdateMenuStockCommand.class))).willThrow(
			new NotEqualsOwnerException("매장 주인이 아닙니다."));

		// when
		mockMvc.perform(patch("/menus/stock")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.session(session))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("메뉴 재고 업데이트 테스트 - 실패(인증되지 않음)")
	void testUpdateMenuStockFailWithUnauthorized() throws Exception {
		// given
		Long menuId = 1L;
		int stock = 10;
		UpdateMenuStockRequest request = new UpdateMenuStockRequest(menuId, stock);

		// when
		mockMvc.perform(patch("/menus/stock")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("메뉴 재고 업데이트 테스트 - 실패(매장이 열려있음)")
	void testUpdateMenuStockFailWithStoreNotOpen() throws Exception {
		// given
		Long menuId = 1L;
		int stock = 10;
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SessionConst.SESSION_VENDOR_KEY, new LoginVendor(UUID.randomUUID()));
		UpdateMenuStockRequest request = new UpdateMenuStockRequest(menuId, stock);

		given(updateMenuStockService.updateMenuStock(any(UpdateMenuStockCommand.class))).willThrow(
			new NotUpdatableTimeException("매장이 열려있습니다."));

		// when
		mockMvc.perform(patch("/menus/stock")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.session(session))
			.andExpect(status().isConflict());
	}

}
