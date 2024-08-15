package camp.woowak.lab.web.api.store;

import static camp.woowak.lab.store.exception.StoreException.ErrorCode.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import camp.woowak.lab.common.exception.UnauthorizedException;
import camp.woowak.lab.infra.date.DateTimeProvider;
import camp.woowak.lab.menu.exception.UnauthorizedMenuCategoryCreationException;
import camp.woowak.lab.menu.service.MenuCategoryRegistrationService;
import camp.woowak.lab.menu.service.command.MenuCategoryRegistrationCommand;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.domain.TestPayAccount;
import camp.woowak.lab.store.exception.StoreException;
import camp.woowak.lab.store.service.StoreRegistrationService;
import camp.woowak.lab.store.service.dto.StoreRegistrationRequest;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.AuthenticationErrorCode;
import camp.woowak.lab.web.authentication.LoginVendor;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;
import camp.woowak.lab.web.dto.request.store.MenuCategoryRegistrationRequest;
import camp.woowak.lab.web.resolver.session.SessionConst;
import camp.woowak.lab.web.resolver.session.SessionVendorArgumentResolver;

@WebMvcTest(StoreApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class StoreApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StoreRegistrationService storeRegistrationService;

	@MockBean
	private MenuCategoryRegistrationService menuCategoryRegistrationService;

	@MockBean
	private VendorRepository vendorRepository;

	@MockBean
	private SessionVendorArgumentResolver sessionVendorArgumentResolver;

	DateTimeProvider fixedStartTime = () -> LocalDateTime.of(2024, 8, 24, 1, 0, 0);
	DateTimeProvider fixedEndTime = () -> LocalDateTime.of(2024, 8, 24, 5, 0, 0);

	LocalDateTime validStartTimeFixture = fixedStartTime.now();
	LocalDateTime validEndTimeFixture = fixedEndTime.now();

	ObjectMapper objectMapper = new ObjectMapper();

	private PayAccount payAccount;
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		payAccount = new TestPayAccount(1L);
		passwordEncoder = new NoOpPasswordEncoder();
	}

	@Nested
	@DisplayName("점포 생성: POST /stores")
	class StoreRegistration {
		@Test
		@DisplayName("[Success] 200 OK")
		void storeRegistrationSuccess() throws Exception {
			// given
			Vendor vendor = createVendor();
			LoginVendor loginVendor = new LoginVendor(UUID.randomUUID());
			StoreRegistrationRequest request = new StoreRegistrationRequest(
				"Store Name",
				"Store Address",
				"123-456-7890",
				"Category Name",
				10000,
				validStartTimeFixture,
				validEndTimeFixture
			);

			given(sessionVendorArgumentResolver.supportsParameter(any()))
				.willReturn(true);
			given(sessionVendorArgumentResolver.resolveArgument(any(), any(), any(), any()))
				.willReturn(loginVendor);
			given(vendorRepository.findById(loginVendor.getId())).willReturn(Optional.of(vendor));

			// when & then
			mockMvc.perform(post("/stores")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					.sessionAttr(SessionConst.SESSION_VENDOR_KEY, loginVendor))
				.andExpect(status().isOk());

			verify(storeRegistrationService).storeRegistration(any(Vendor.class), any(StoreRegistrationRequest.class));
		}

		@Disabled
		@Test
		@DisplayName("[Exception] 400 Bad Request")
		void storeRegistrationFailure() throws Exception {
			// given
			Vendor vendor = createVendor();
			LoginVendor loginVendor = new LoginVendor(UUID.randomUUID());
			StoreRegistrationRequest request = new StoreRegistrationRequest(
				"Store Name",
				"Invalid Category",
				"Store Address",
				"123-456-7890",
				10000,
				validStartTimeFixture,
				validEndTimeFixture
			);

			doThrow(new StoreException(INVALID_STORE_CATEGORY))
				.when(storeRegistrationService)
				.storeRegistration(any(Vendor.class), any(StoreRegistrationRequest.class));

			// when & then
			mockMvc.perform(post("/stores")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					.sessionAttr(SessionConst.SESSION_VENDOR_KEY, loginVendor))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("fail"));

			verify(storeRegistrationService).storeRegistration(any(Vendor.class), any(StoreRegistrationRequest.class));
		}
	}

	@Nested
	@DisplayName("메뉴 카테고리 생성: POST /stores/{storeId}/category")
	class StoreCategoryRegistration {
		@Test
		@DisplayName("[Success] 200 OK")
		void success() throws Exception {
			// given
			LoginVendor loginVendor = new LoginVendor(UUID.randomUUID());

			// when
			when(sessionVendorArgumentResolver.supportsParameter(any()))
				.thenReturn(true);
			when(sessionVendorArgumentResolver.resolveArgument(any(), any(), any(), any()))
				.thenReturn(loginVendor);
			MenuCategoryRegistrationRequest request = new MenuCategoryRegistrationRequest("아주 싱싱한 회");

			// then
			mockMvc.perform(post("/stores/" + new Random().nextLong() + "/category")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					.sessionAttr(SessionConst.SESSION_VENDOR_KEY, loginVendor))
				.andExpect(status().isOk());

			verify(menuCategoryRegistrationService).register(any(MenuCategoryRegistrationCommand.class));
		}

		@Test
		@DisplayName("[Exception] 400 Bad Request")
		void failWith400() throws Exception {
			// given
			LoginVendor loginVendor = new LoginVendor(UUID.randomUUID());

			// when & then
			when(sessionVendorArgumentResolver.supportsParameter(any()))
				.thenReturn(true);
			when(sessionVendorArgumentResolver.resolveArgument(any(), any(), any(), any()))
				.thenReturn(loginVendor);
			MenuCategoryRegistrationRequest request = new MenuCategoryRegistrationRequest(null);

			// then
			mockMvc.perform(post("/stores/" + new Random().nextLong() + "/category")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					.sessionAttr(SessionConst.SESSION_VENDOR_KEY, loginVendor))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("[Exception] 401 Unauthenticated")
		void failWith401() throws Exception {
			// given

			// when
			when(sessionVendorArgumentResolver.supportsParameter(any()))
				.thenReturn(false);
			when(sessionVendorArgumentResolver.resolveArgument(any(), any(), any(), any()))
				.thenThrow(
					new UnauthorizedException(AuthenticationErrorCode.UNAUTHORIZED, "Vendor가 세션에 저장되어 있지 않습니다."));
			MenuCategoryRegistrationRequest request = new MenuCategoryRegistrationRequest("아주 싱싱한 회");

			// then
			mockMvc.perform(post("/stores/" + new Random().nextLong() + "/category")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnauthorized());
		}

		@Test
		@DisplayName("[Exception] 403 Forbidden")
		void failWith403() throws Exception {
			// given
			LoginVendor loginVendor = new LoginVendor(UUID.randomUUID());

			// when
			when(sessionVendorArgumentResolver.supportsParameter(any()))
				.thenReturn(true);
			when(sessionVendorArgumentResolver.resolveArgument(any(), any(), any(), any()))
				.thenReturn(loginVendor);
			when(menuCategoryRegistrationService.register(any(MenuCategoryRegistrationCommand.class))).thenThrow(
				new UnauthorizedMenuCategoryCreationException("권한없는 사용자가 메뉴 카테고리 등록을 시도했습니다."));
			MenuCategoryRegistrationRequest request = new MenuCategoryRegistrationRequest("아주 싱싱한 회");

			// then
			mockMvc.perform(post("/stores/" + new Random().nextLong() + "/category")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					.sessionAttr(SessionConst.SESSION_VENDOR_KEY, loginVendor))
				.andExpect(status().isForbidden());

			verify(menuCategoryRegistrationService).register(any(MenuCategoryRegistrationCommand.class));
		}
	}

	private Vendor createVendor() {
		return new Vendor("vendorName", "vendorEmail@example.com", "vendorPassword", "010-0000-0000", payAccount,
			passwordEncoder);
	}
}
