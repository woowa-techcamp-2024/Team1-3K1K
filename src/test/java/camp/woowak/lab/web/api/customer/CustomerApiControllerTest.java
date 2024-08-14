package camp.woowak.lab.web.api.customer;

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
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import camp.woowak.lab.customer.exception.CustomerErrorCode;
import camp.woowak.lab.customer.exception.DuplicateEmailException;
import camp.woowak.lab.customer.service.SignUpCustomerService;
import camp.woowak.lab.web.dto.request.customer.SignUpCustomerRequest;

@WebMvcTest(CustomerApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CustomerApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SignUpCustomerService signUpCustomerService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("구매자 회원가입 테스트 - 성공")
	void testSignUpCustomer() throws Exception {
		// given
		String customerId = UUID.randomUUID().toString();
		SignUpCustomerRequest request = new SignUpCustomerRequest("name", "email@test.com", "password123",
			"010-1234-5678");
		given(signUpCustomerService.signUp(any())).willReturn(customerId);

		// when & then
		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("구매자 회원가입 테스트 - 이름이 없는 경우")
	void testSignUpCustomerWithoutName() throws Exception {
		// given
		SignUpCustomerRequest request = new SignUpCustomerRequest("", "email@test.com", "password123", "010-1234-5678");

		// when & then
		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("구매자 회원가입 테스트 - 이메일이 없는 경우")
	void testSignUpCustomerWithoutEmail() throws Exception {
		// given
		SignUpCustomerRequest request = new SignUpCustomerRequest("name", "", "password123", "010-1234-5678");

		// when & then
		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("구매자 회원가입 테스트 - 비밀번호가 없는 경우")
	void testSignUpCustomerWithoutPassword() throws Exception {
		// given
		SignUpCustomerRequest request = new SignUpCustomerRequest("name", "email@test.com", "", "010-1234-5678");

		// when & then
		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("구매자 회원가입 테스트 - 전화번호가 없는 경우")
	void testSignUpCustomerWithoutPhone() throws Exception {
		// given
		SignUpCustomerRequest request = new SignUpCustomerRequest("name", "email@test.com", "password123", "");

		// when & then
		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("구매자 회원가입 테스트 - 이름이 50자 초과인 경우")
	void testSignUpCustomerWithLongName() throws Exception {
		// given
		SignUpCustomerRequest request = new SignUpCustomerRequest("n".repeat(51), "email@test.com", "password123",
			"010-1234-5678");

		// when & then
		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("구매자 회원가입 테스트 - 이메일이 100자 초과인 경우")
	void testSignUpCustomerWithLongEmail() throws Exception {
		// given
		SignUpCustomerRequest request = new SignUpCustomerRequest("name", "e".repeat(90) + "@test.com", "password123",
			"010-1234-5678");

		// when & then
		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("구매자 회원가입 테스트 - 비밀번호가 20자 초과인 경우")
	void testSignUpCustomerWithLongPassword() throws Exception {
		SignUpCustomerRequest request = new SignUpCustomerRequest("name", "email@test.com", "p".repeat(21),
			"010-1234-5678");

		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("구매자 회원가입 테스트 - 비밀번호가 8자 미만인 경우")
	void testSignUpCustomerWithShortPassword() throws Exception {
		// given
		SignUpCustomerRequest request = new SignUpCustomerRequest("name", "email@test.com", "pass", "010-1234-5678");

		// when & then
		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("구매자 회원가입 테스트 - 전화번호가 30자 초과인 경우")
	void testSignUpCustomerWithLongPhone() throws Exception {
		// given
		SignUpCustomerRequest request = new SignUpCustomerRequest("name", "email@test.com", "password123",
			"0".repeat(31));

		// when & then
		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("구매자 회원가입 테스트 - 이메일 형식이 아닌 경우")
	void testSignUpCustomerWithInvalidEmail() throws Exception {
		// given
		SignUpCustomerRequest request = new SignUpCustomerRequest("name", "invalid-email", "password123",
			"010-1234-5678");

		// when & then
		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("구매자 회원가입 테스트 - 전화번호 형식이 아닌 경우")
	void testSignUpCustomerWithInvalidPhone() throws Exception {
		// given
		SignUpCustomerRequest request = new SignUpCustomerRequest("name", "email@test.com", "password123",
			"invalid-phone");

		// when & then
		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("구매자 회원가입 테스트 - 중복된 이메일인 경우")
	void testSignUpCustomerWithDuplicateEmail() throws Exception {
		// given
		SignUpCustomerRequest request = new SignUpCustomerRequest("name", "duplicate@test.com", "password123",
			"010-1234-5678");
		given(signUpCustomerService.signUp(any())).willThrow(new DuplicateEmailException());

		// when & then
		mockMvc.perform(post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.errorCode").value(CustomerErrorCode.DUPLICATE_EMAIL.getErrorCode()))
			.andExpect(jsonPath("$.detail").value(CustomerErrorCode.DUPLICATE_EMAIL.getMessage()));
	}
}
