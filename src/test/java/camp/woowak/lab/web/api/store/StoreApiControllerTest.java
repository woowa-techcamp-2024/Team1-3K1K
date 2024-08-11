package camp.woowak.lab.web.api.store;

import static camp.woowak.lab.store.exception.StoreException.ErrorCode.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import camp.woowak.lab.infra.date.DateTimeProvider;
import camp.woowak.lab.store.exception.StoreException;
import camp.woowak.lab.store.service.StoreRegistrationService;
import camp.woowak.lab.store.service.dto.StoreRegistrationRequest;
import camp.woowak.lab.vendor.domain.Vendor;

@WebMvcTest(StoreApiController.class)
class StoreApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StoreRegistrationService storeRegistrationService;

	DateTimeProvider fixedStartTime = () -> LocalDateTime.of(2024, 8, 24, 1, 0, 0);
	DateTimeProvider fixedEndTime = () -> LocalDateTime.of(2024, 8, 24, 5, 0, 0);

	LocalDateTime validStartTimeFixture = fixedStartTime.now();
	LocalDateTime validEndTimeFixture = fixedEndTime.now();

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Test
	@DisplayName("[Success] 200 OK")
	void storeRegistrationSuccess() throws Exception {
		// given
		Vendor vendor = new Vendor();
		StoreRegistrationRequest request = new StoreRegistrationRequest(
			"Store Name",
			"Category Name",
			"Store Address",
			"123-456-7890",
			10000,
			validStartTimeFixture,
			validEndTimeFixture
		);

		// when & then
		mockMvc.perform(post("/stores")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.sessionAttr("vendor", vendor)) // TODO: 세션/JWT 인증 방식 토의 후, 정해진 방식에 맞게 수정 필요 (현재는 세션 방식으로 단위 테스트 진행)
			.andExpect(status().isOk())
			.andExpect(content().string(""));

		verify(storeRegistrationService).storeRegistration(any(Vendor.class), any(StoreRegistrationRequest.class));
	}

	@Test
	@DisplayName("[Exception] 400 Bad Request")
	void storeRegistrationFailure() throws Exception {
		// given
		Vendor vendor = new Vendor(); // Vendor 객체 생성 방법에 따라 적절히 초기화
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
			.when(storeRegistrationService).storeRegistration(any(Vendor.class), any(StoreRegistrationRequest.class));

		// when & then
		mockMvc.perform(post("/stores")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.sessionAttr("vendor", vendor)) // TODO: 세션/JWT 인증 방식 토의 후, 정해진 방식에 맞게 수정 필요 (현재는 세션 방식으로 단위 테스트 진행)
			.andExpect(status().isBadRequest())
			.andExpect(content().string("fail"));

		verify(storeRegistrationService).storeRegistration(any(Vendor.class), any(StoreRegistrationRequest.class));
	}
}