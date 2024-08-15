package camp.woowak.lab.web.api.coupon;

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

import camp.woowak.lab.coupon.service.IssueCouponService;
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
}