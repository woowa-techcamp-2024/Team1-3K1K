package camp.woowak.lab.web.api.vendor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
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

import camp.woowak.lab.fixture.VendorFixture;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.exception.DuplicateEmailException;
import camp.woowak.lab.vendor.exception.NotFoundVendorException;
import camp.woowak.lab.vendor.exception.PasswordMismatchException;
import camp.woowak.lab.vendor.service.RetrieveVendorService;
import camp.woowak.lab.vendor.service.SignInVendorService;
import camp.woowak.lab.vendor.service.SignUpVendorService;
import camp.woowak.lab.vendor.service.command.SignInVendorCommand;
import camp.woowak.lab.vendor.service.command.SignUpVendorCommand;
import camp.woowak.lab.vendor.service.dto.VendorDTO;
import camp.woowak.lab.web.authentication.LoginVendor;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.dto.request.vendor.SignInVendorRequest;
import camp.woowak.lab.web.dto.request.vendor.SignUpVendorRequest;
import camp.woowak.lab.web.resolver.session.SessionConst;
import jakarta.servlet.http.HttpSession;

@WebMvcTest(controllers = VendorApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class VendorApiControllerTest implements VendorFixture {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private SignUpVendorService signUpVendorService;
	@MockBean
	private SignInVendorService signInVendorService;
	@MockBean
	private RetrieveVendorService retrieveVendorService;

	@Nested
	@DisplayName("판매자 회원가입: POST /vendors")
	class SignUpVendor {
		@Test
		@DisplayName("[성공] 201")
		void success() throws Exception {
			UUID fakeVendorId = UUID.randomUUID();
			BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
				.willReturn(fakeVendorId.toString());

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
				.andExpect(jsonPath("$.data.id").value(fakeVendorId.toString()))
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
					UUID fakeVendorId = UUID.randomUUID();
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId.toString());

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
					UUID fakeVendorId = UUID.randomUUID();
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId.toString());

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
					UUID fakeVendorId = UUID.randomUUID();
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId.toString());

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
					UUID fakeVendorId = UUID.randomUUID();
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId.toString());

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
					UUID fakeVendorId = UUID.randomUUID();
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId.toString());

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
					UUID fakeVendorId = UUID.randomUUID();
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId.toString());

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
					UUID fakeVendorId = UUID.randomUUID();
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId.toString());

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
					UUID fakeVendorId = UUID.randomUUID();
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId.toString());

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
					UUID fakeVendorId = UUID.randomUUID();
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId.toString());

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
					UUID fakeVendorId = UUID.randomUUID();
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId.toString());

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
					UUID fakeVendorId = UUID.randomUUID();
					BDDMockito.given(signUpVendorService.signUp(BDDMockito.any(SignUpVendorCommand.class)))
						.willReturn(fakeVendorId.toString());

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

	@Nested
	@DisplayName("판매자 로그인: POST /vendors/login")
	class SignInVendor {
		@Test
		@DisplayName("[성공] 204")
		void success() throws Exception {
			UUID fakeVendorId = UUID.randomUUID();
			BDDMockito.given(signInVendorService.signIn(BDDMockito.any(SignInVendorCommand.class)))
				.willReturn(fakeVendorId);

			// when
			ResultActions actions = mockMvc.perform(
				post("/vendors/login")
					.content(new ObjectMapper().writeValueAsString(
						new SignInVendorRequest("validEmail@validEmail.com", "validPassword")))
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
			);

			// then
			actions.andExpect(status().isNoContent())
				.andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
				.andExpect(result -> {
					HttpSession session = result.getRequest().getSession();
					LoginVendor loginVendor = (LoginVendor)session.getAttribute(SessionConst.SESSION_VENDOR_KEY);
					Assertions.assertNotNull(loginVendor);
					Assertions.assertEquals(loginVendor.getId(), fakeVendorId);
				})
				.andDo(print());
		}

		@Test
		@DisplayName("[실패] 400 : 존재하지 않는 판매자 이메일")
		void failWithNotFoundVendor() throws Exception {
			UUID fakeVendorId = UUID.randomUUID();
			BDDMockito.given(signInVendorService.signIn(BDDMockito.any(SignInVendorCommand.class)))
				.willThrow(NotFoundVendorException.class);

			// when
			ResultActions actions = mockMvc.perform(
				post("/vendors/login")
					.content(new ObjectMapper().writeValueAsString(
						new SignInVendorRequest("notFound@validEmail.com", "validPassword")))
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
			);

			// then
			actions.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
				.andDo(print());
		}

		@Test
		@DisplayName("[실패] 400 : 잘못된 비밀번호")
		void failWithPasswordMismatch() throws Exception {
			UUID fakeVendorId = UUID.randomUUID();
			BDDMockito.given(signInVendorService.signIn(BDDMockito.any(SignInVendorCommand.class)))
				.willThrow(PasswordMismatchException.class);

			// when
			ResultActions actions = mockMvc.perform(
				post("/vendors/login")
					.content(new ObjectMapper().writeValueAsString(
						new SignInVendorRequest("validEmail@validEmail.com", "wrongPassword")))
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
			);

			// then
			actions.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
				.andDo(print());
		}
	}

	@Nested
	@DisplayName("전체 판매자 조회: GET /vendors")
	class FindVendor {
		@Test
		@DisplayName("[성공] 200")
		void success() throws Exception {
			// given
			List<Vendor> vendors = List.of(
				createVendor(createPayAccount(), new NoOpPasswordEncoder()),
				createVendor(createPayAccount(), new NoOpPasswordEncoder())
			);
			BDDMockito.given(retrieveVendorService.retrieveVendors())
				.willReturn(vendors.stream().map(VendorDTO::new).toList());

			// when
			ResultActions actions = mockMvc.perform(
				get("/vendors")
			);

			// then
			actions.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.vendors").isArray())
				.andExpect(jsonPath("$.data.vendors.length()").value(vendors.size()));

			for (int i = 0; i < vendors.size(); i++) {
				actions
					.andExpect(jsonPath("$.data.vendors[" + i + "].name").value(vendors.get(i).getName()))
					.andExpect(jsonPath("$.data.vendors[" + i + "].email").value(vendors.get(i).getEmail()))
					.andExpect(jsonPath("$.data.vendors[" + i + "].phone").value(vendors.get(i).getPhone()))
					.andExpect(jsonPath("$.data.vendors[" + i + "].payAccount.balance")
						.value(vendors.get(i).getPayAccount().getBalance()));
			}
			actions.andDo(print());
		}
	}
}
