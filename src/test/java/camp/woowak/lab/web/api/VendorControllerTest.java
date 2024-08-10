package camp.woowak.lab.web.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import camp.woowak.lab.vendor.exception.DuplicateEmailException;
import camp.woowak.lab.vendor.service.SignUpVendorService;
import camp.woowak.lab.vendor.service.command.SignUpVendorCommand;
import camp.woowak.lab.web.dto.request.SignUpVendorRequest;

@WebMvcTest(controllers = VendorController.class)
@MockBean(JpaMetamodelMappingContext.class)
class VendorControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private SignUpVendorService signUpVendorService;

	@Nested
	@DisplayName("판매자 회원가입: POST /vendors")
	class SignUpVendor {
		@Test
		@DisplayName("[성공] 201")
		void success() throws Exception {
			long fakeVendorId = new Random().nextLong(1000L);
			BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
				.willReturn(fakeVendorId);

			// when
			ResultActions actions = mockMvc.perform(
				post("/vendors")
					.content(new ObjectMapper().writeValueAsString(
						new SignUpVendorRequest("validName", "validEmail@validEmail.com", "validPassword",
							"010-0000-0000")))
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
			);

			// then
			actions.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.header().string("location", "/vendors/" + fakeVendorId))
				.andDo(print());
		}

		@Test
		@DisplayName("[실패] 400 : 이름이 비어있는 경우")
		void failWithInvalidName() throws Exception {
			long fakeVendorId = new Random().nextLong(1000L);
			BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
				.willReturn(fakeVendorId);

			// when
			ResultActions actions = mockMvc.perform(
				post("/vendors")
					.content(new ObjectMapper().writeValueAsString(
						new SignUpVendorRequest("", "validEmail@validEmail.com", "validPassword",
							"010-0000-0000")))
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
			);

			// then
			actions.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Bad Request"))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.instance").value("/vendors"))
				.andDo(print());
		}

		@Test
		@DisplayName("[실패] 400 : 불가능한 전화번호")
		void failWithInvalidPhone() throws Exception {
			long fakeVendorId = new Random().nextLong(1000L);
			BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
				.willReturn(fakeVendorId);

			// when
			ResultActions actions = mockMvc.perform(
				post("/vendors")
					.content(new ObjectMapper().writeValueAsString(
						new SignUpVendorRequest("", "validEmail@validEmail.com", "validPassword",
							"111-1111-0000")))
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
			);

			// then
			actions.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Bad Request"))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.instance").value("/vendors"))
				.andDo(print());
		}

		@Test
		@DisplayName("[실패] 200 a1: 이미 가입된 이메일인 경우")
		void failWithDuplicateEmail() throws Exception {
			BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
				.willThrow(DuplicateEmailException.class);

			// when
			ResultActions actions = mockMvc.perform(
				post("/vendors")
					.content(new ObjectMapper().writeValueAsString(
						new SignUpVendorRequest("validName", "validEmail@validEmail.com", "validPassword",
							"010-0000-0000")))
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
			);

			// then
			actions.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value("a1"))
				.andExpect(jsonPath("$.message").value("이미 가입된 이메일 입니다."))
				.andExpect(jsonPath("$.data").isEmpty())
				.andDo(print());
		}
	}
}
