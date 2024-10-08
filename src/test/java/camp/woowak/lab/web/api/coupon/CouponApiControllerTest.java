package camp.woowak.lab.web.api.coupon;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
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

import camp.woowak.lab.coupon.exception.DuplicateCouponTitleException;
import camp.woowak.lab.coupon.exception.ExpiredCouponException;
import camp.woowak.lab.coupon.exception.InsufficientCouponQuantityException;
import camp.woowak.lab.coupon.exception.InvalidICreationIssuanceException;
import camp.woowak.lab.coupon.service.CreateCouponService;
import camp.woowak.lab.coupon.service.IssueCouponService;
import camp.woowak.lab.coupon.service.command.CreateCouponCommand;
import camp.woowak.lab.coupon.service.command.IssueCouponCommand;
import camp.woowak.lab.web.authentication.LoginCustomer;
import camp.woowak.lab.web.dto.request.coupon.CreateCouponRequest;
import camp.woowak.lab.web.resolver.session.SessionConst;

@WebMvcTest(CouponApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CouponApiControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CreateCouponService createCouponService;

	@MockBean
	private IssueCouponService issueCouponService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("쿠폰 등록 테스트 - 성공")
	void testCreateCoupon() throws Exception {
		// given
		given(createCouponService.createCoupon(any(CreateCouponCommand.class))).willReturn(1L);

		mockMvc.perform(post("/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new CreateCouponRequest("테스트 쿠폰", 1000, 100, LocalDateTime.now().plusDays(7))))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.couponId").exists());
	}

	@Test
	@DisplayName("쿠폰 등록 테스트 - 잘못된 제목 입력 시 실패")
	void testCreateCouponFailWithInvalidTitle() throws Exception {
		mockMvc.perform(post("/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new CreateCouponRequest("", 1000, 100, LocalDateTime.now().plusDays(7))))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("쿠폰 등록 테스트 - 잘못된 할인 금액 입력 시 실패")
	void testCreateCouponFailWithInvalidDiscountAmount() throws Exception {
		mockMvc.perform(post("/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new CreateCouponRequest("테스트 쿠폰", -1, 100, LocalDateTime.now().plusDays(7))))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("쿠폰 등록 테스트 - 잘못된 수량 입력 시 실패")
	void testCreateCouponFailWithInvalidQuantity() throws Exception {
		mockMvc.perform(post("/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new CreateCouponRequest("테스트 쿠폰", 1000, -1, LocalDateTime.now().plusDays(7))))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("쿠폰 등록 테스트 - 잘못된 만료일 입력 시 실패")
	void testCreateCouponFailWithInvalidExpiredAt() throws Exception {
		mockMvc.perform(post("/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new CreateCouponRequest("테스트 쿠폰", 1000, 100, LocalDateTime.now().minusDays(7))))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("쿠폰 등록 테스트 - 중복된 제목 입력 시 실패")
	void testCreateCouponFailWithDuplicateTitle() throws Exception {
		// given
		CreateCouponRequest request = new CreateCouponRequest("테스트 쿠폰", 1000, 100, LocalDateTime.now().plusDays(7));
		given(createCouponService.createCoupon(any(CreateCouponCommand.class)))
			.willThrow(new DuplicateCouponTitleException("중복된 쿠폰 제목입니다."));

		// when & then
		mockMvc.perform(post("/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					request))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isConflict());
	}

	@Test
	@DisplayName("쿠폰 발급 테스트 - 성공")
	void testIssueCoupon() throws Exception {
		// given
		Long couponId = 1L;
		UUID customerId = UUID.randomUUID();
		LoginCustomer loginCustomer = new LoginCustomer(customerId);

		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SessionConst.SESSION_CUSTOMER_KEY, loginCustomer);

		given(issueCouponService.issueCoupon(any(IssueCouponCommand.class))).willReturn(1L);

		// when & then
		mockMvc.perform(post("/coupons/" + couponId + "/issue")
				.session(session) // 세션 설정
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("쿠폰 발급 테스트 - 세션 없이 요청 시 실패")
	void testIssueCouponFailWithoutSession() throws Exception {
		// given
		Long couponId = 1L;

		// when & then
		mockMvc.perform(post("/coupons/" + couponId + "/issue")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("쿠폰 발급 테스트 - 존재하지 않는 쿠폰 or 구매자 ID 입력 시 실패")
	void testIssueCouponFailWithNotExistsId() throws Exception {
		// given
		UUID customerId = UUID.randomUUID();
		LoginCustomer loginCustomer = new LoginCustomer(customerId);

		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SessionConst.SESSION_CUSTOMER_KEY, loginCustomer);
		given(issueCouponService.issueCouponWithDistributionLock(any(IssueCouponCommand.class)))
			.willThrow(new InvalidICreationIssuanceException("존재하지 않는 쿠폰 or 구매자 ID 입력입니다."));
		// when & then
		mockMvc.perform(post("/coupons/999/issue")
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("쿠폰 발급 테스트 - 쿠폰 만료 실패")
	void testIssueCouponFailWithExpiredCoupon() throws Exception {
		// given
		UUID customerId = UUID.randomUUID();
		LoginCustomer loginCustomer = new LoginCustomer(customerId);

		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SessionConst.SESSION_CUSTOMER_KEY, loginCustomer);
		given(issueCouponService.issueCouponWithDistributionLock(any(IssueCouponCommand.class)))
			.willThrow(new ExpiredCouponException("쿠폰이 만료되었습니다."));
		// when & then
		mockMvc.perform(post("/coupons/999/issue")
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isConflict());
	}

	@Test
	@DisplayName("쿠폰 발급 테스트 - 수량 부족 실패")
	void testIssueCouponFailWithInsufficientQuantity() throws Exception {
		// given
		UUID customerId = UUID.randomUUID();
		LoginCustomer loginCustomer = new LoginCustomer(customerId);

		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SessionConst.SESSION_CUSTOMER_KEY, loginCustomer);
		given(issueCouponService.issueCouponWithDistributionLock(any(IssueCouponCommand.class)))
			.willThrow(new InsufficientCouponQuantityException("수량이 부족합니다."));
		// when & then
		mockMvc.perform(post("/coupons/999/issue")
				.session(session) // 세션 설정
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isConflict());
	}
}
