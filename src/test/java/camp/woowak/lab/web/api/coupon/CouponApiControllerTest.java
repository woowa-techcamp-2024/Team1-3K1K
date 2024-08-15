package camp.woowak.lab.web.api.coupon;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import camp.woowak.lab.coupon.exception.DuplicateCouponTitleException;
import camp.woowak.lab.coupon.service.IssueCouponService;
import camp.woowak.lab.coupon.service.command.IssueCouponCommand;
import camp.woowak.lab.web.dto.request.coupon.IssueCouponRequest;

@WebMvcTest(CouponApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CouponApiControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IssueCouponService issueCouponService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("쿠폰 발급 테스트 - 성공")
	void testIssueCoupon() throws Exception {
		mockMvc.perform(post("/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new IssueCouponRequest("테스트 쿠폰", 1000, 100, LocalDateTime.now().plusDays(7))))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.couponId").exists());
	}

	@Test
	@DisplayName("쿠폰 발급 테스트 - 잘못된 제목 입력 시 실패")
	void testIssueCouponFailWithInvalidTitle() throws Exception {
		mockMvc.perform(post("/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new IssueCouponRequest("", 1000, 100, LocalDateTime.now().plusDays(7))))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("쿠폰 발급 테스트 - 잘못된 할인 금액 입력 시 실패")
	void testIssueCouponFailWithInvalidDiscountAmount() throws Exception {
		mockMvc.perform(post("/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new IssueCouponRequest("테스트 쿠폰", -1, 100, LocalDateTime.now().plusDays(7))))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("쿠폰 발급 테스트 - 잘못된 수량 입력 시 실패")
	void testIssueCouponFailWithInvalidQuantity() throws Exception {
		mockMvc.perform(post("/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new IssueCouponRequest("테스트 쿠폰", 1000, -1, LocalDateTime.now().plusDays(7))))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("쿠폰 발급 테스트 - 잘못된 만료일 입력 시 실패")
	void testIssueCouponFailWithInvalidExpiredAt() throws Exception {
		mockMvc.perform(post("/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new IssueCouponRequest("테스트 쿠폰", 1000, 100, LocalDateTime.now().minusDays(7))))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("쿠폰 발급 테스트 - 중복된 제목 입력 시 실패")
	void testIssueCouponFailWithDuplicateTitle() throws Exception {
		// given
		IssueCouponRequest request = new IssueCouponRequest("테스트 쿠폰", 1000, 100, LocalDateTime.now().plusDays(7));
		given(issueCouponService.issueCoupon(any(IssueCouponCommand.class)))
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
}
