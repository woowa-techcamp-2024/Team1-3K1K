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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import camp.woowak.lab.vendor.exception.DuplicateEmailException;
import camp.woowak.lab.vendor.service.SignUpVendorService;
import camp.woowak.lab.vendor.service.command.SignUpVendorCommand;
import camp.woowak.lab.web.api.vendor.VendorApiController;
import camp.woowak.lab.web.dto.request.vendor.SignUpVendorRequest;

@WebMvcTest(controllers = VendorApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class VendorApiControllerTest {
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
				.andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
				.andExpect(jsonPath("$.data.id").value(fakeVendorId))
				.andDo(print());
		}

		@Nested
		@DisplayName("[실패] 400")
		class FailWith400 {
			@Nested
			@DisplayName("이름이")
			class NameMust {
				@Test
				@DisplayName("비어있는 경우")
				void failWithEmptyName() throws Exception {
					long fakeVendorId = new Random().nextLong(1000L);
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId);

					// when
					ResultActions actions = mockMvc.perform(
						post("/vendors")
							.content(new ObjectMapper().writeValueAsString(
								new SignUpVendorRequest(null, "validEmail@validEmail.com", "validPassword",
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
				@DisplayName("공란인 경우")
				void failWithBlankName() throws Exception {
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
			}

			@Nested
			@DisplayName("이메일이")
			class EmailMust {
				@Test
				@DisplayName("비어있는 경우")
				void failWithEmptyEmail() throws Exception {
					long fakeVendorId = new Random().nextLong(1000L);
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId);

					// when
					ResultActions actions = mockMvc.perform(
						post("/vendors")
							.content(new ObjectMapper().writeValueAsString(
								new SignUpVendorRequest("validName", null, "validPassword", "010-0000-0000")))
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
				@DisplayName("공란인 경우")
				void failWithBlankEmail() throws Exception {
					long fakeVendorId = new Random().nextLong(1000L);
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId);

					// when
					ResultActions actions = mockMvc.perform(
						post("/vendors")
							.content(new ObjectMapper().writeValueAsString(
								new SignUpVendorRequest("validName", "", "validPassword", "010-0000-0000")))
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
			}

			@Nested
			@DisplayName("비밀번호가")
			class PasswordMust {
				@Test
				@DisplayName("비어있는 경우")
				void failWithEmptyPassword() throws Exception {
					long fakeVendorId = new Random().nextLong(1000L);
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId);

					// when
					ResultActions actions = mockMvc.perform(
						post("/vendors")
							.content(new ObjectMapper().writeValueAsString(
								new SignUpVendorRequest("validName", "validEmail@validEmail.com", null,
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
				@DisplayName("공란인 경우")
				void failWithBlankPassword() throws Exception {
					long fakeVendorId = new Random().nextLong(1000L);
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId);

					// when
					ResultActions actions = mockMvc.perform(
						post("/vendors")
							.content(new ObjectMapper().writeValueAsString(
								new SignUpVendorRequest("validName", "validEmail@validEmail.com", "",
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
				@DisplayName("8자 미만인 경우")
				void failWith7Password() throws Exception {
					long fakeVendorId = new Random().nextLong(1000L);
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId);

					// when
					ResultActions actions = mockMvc.perform(
						post("/vendors")
							.content(new ObjectMapper().writeValueAsString(
								new SignUpVendorRequest("validName", "validEmail@validEmail.com", "abcdefg",
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
				@DisplayName("30자 초과인 경우")
				void failWith31Password() throws Exception {
					long fakeVendorId = new Random().nextLong(1000L);
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId);

					// when
					ResultActions actions = mockMvc.perform(
						post("/vendors")
							.content(new ObjectMapper().writeValueAsString(
								new SignUpVendorRequest("validName", "validEmail@validEmail.com",
									"aaaaaaaaaabbbbbbbbbbccccccccccd",
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
			}

			@Nested
			@DisplayName("전화번호가")
			class PhoneMust {
				@Test
				@DisplayName("비어있는 경우")
				void failWithEmptyPhone() throws Exception {
					long fakeVendorId = new Random().nextLong(1000L);
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId);

					// when
					ResultActions actions = mockMvc.perform(
						post("/vendors")
							.content(new ObjectMapper().writeValueAsString(
								new SignUpVendorRequest("validName", "validEmail@validEmail.com", "validPassword",
									null)))
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
				@DisplayName("공란인 경우")
				void failWithBlankPhone() throws Exception {
					long fakeVendorId = new Random().nextLong(1000L);
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId);

					// when
					ResultActions actions = mockMvc.perform(
						post("/vendors")
							.content(new ObjectMapper().writeValueAsString(
								new SignUpVendorRequest("validName", "validEmail@validEmail.com", "validPassword", "")))
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
				@DisplayName("잘못된 형식인 경우")
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
			}
		}

		@Test
		@DisplayName("[실패] 400 : 이미 가입된 이메일인 경우")
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
			actions.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.type").value("about:blank"))
				.andExpect(jsonPath("$.title").value("Bad Request"))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.instance").value("/vendors"))
				.andDo(print());
		}
	}
}
