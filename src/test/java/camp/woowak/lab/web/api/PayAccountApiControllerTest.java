package camp.woowak.lab.web.api;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import camp.woowak.lab.common.exception.ErrorCode;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.exception.PayAccountErrorCode;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.payaccount.service.PayAccountChargeService;
import camp.woowak.lab.payaccount.service.command.PayAccountChargeCommand;
import camp.woowak.lab.web.authentication.LoginCustomer;
import camp.woowak.lab.web.dto.request.payaccount.PayAccountChargeRequest;
import camp.woowak.lab.web.resolver.session.SessionConst;

@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("PayAccountApiController 클래스")
class PayAccountApiControllerTest {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private PayAccountRepository payAccountRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private PayAccountChargeService payAccountChargeService;

	@Autowired
	private ObjectMapper objectMapper;

	private MockHttpSession session;
	private Customer customer;
	private PayAccount payAccount;
	private long originBalance;

	@BeforeEach
	void setUp() throws Exception {
		originBalance = 1000L;
		payAccount = new PayAccount();
		payAccount.deposit(originBalance);
		payAccountRepository.saveAndFlush(payAccount);

		customer = new Customer(payAccount);
		customerRepository.saveAndFlush(customer);

		session = new MockHttpSession();
		session.setAttribute(SessionConst.SESSION_CUSTOMER_KEY, new LoginCustomer(customer.getId()));
	}

	@AfterEach
	void clearSession() throws Exception {
		session.clearAttributes();
	}

	private void verificationPersistedBalance(Long payAccountId, long amount) {
		Optional<PayAccount> byId = payAccountRepository.findById(payAccountId);
		assertThat(byId).isPresent();
		PayAccount persistedAccount = byId.get();
		assertThat(persistedAccount.getBalance()).isEqualTo(amount);
	}

	@Nested
	@DisplayName("충전 요청은")
	class PayAccountChargeAPITest {
		private final long DAILY_LIMIT = 1_000_000L;
		private final String BASE_URL = "/account/charge";

		@Test
		@DisplayName("존재하는 계정 ID에 정상범위의 금액을 입력하면 충전된다.")
		void successTest() throws Exception {
			//given
			long amount = 1000L;
			PayAccountChargeRequest command = new PayAccountChargeRequest(amount);

			//when & then
			mvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsBytes(command))
					.session(session))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
				.andExpect(jsonPath("$.data.balance").value(amount + originBalance));

			verificationPersistedBalance(payAccount.getId(), amount + originBalance);
		}

		@Test
		@DisplayName("일일 한도(100만원) 이상인 경우, 400을 return한다.")
		void dailyLimitExceededTest() throws Exception {
			//given
			long amount = 1000L;
			payAccountChargeService.chargeAccount(new PayAccountChargeCommand(customer.getId(), DAILY_LIMIT));
			PayAccountChargeRequest command = new PayAccountChargeRequest(amount);

			//when & then
			ResultActions actions = mvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsBytes(command))
					.session(session))
				.andDo(print())
				.andExpect(status().isBadRequest());

			validateErrorResponseWithErrorCode(actions, PayAccountErrorCode.DAILY_LIMIT_EXCEED);
		}

		//TODO : 아직 API Response Format이 정해지지 않았으므로, 논의 후 추가
		@Test
		@DisplayName("존재하지 않는 계정 ID를 입력하면 404를 return한다.")
		void notExistsAccountIdTest() throws Exception {
			//given
			long amount = 1000L;
			Long notExistsId = Long.MAX_VALUE;
			MockHttpSession notExistsSession = new MockHttpSession();
			notExistsSession.setAttribute(SessionConst.SESSION_CUSTOMER_KEY, new LoginCustomer(notExistsId));
			PayAccountChargeRequest command = new PayAccountChargeRequest(amount);

			//when & then
			ResultActions actions = mvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsBytes(command))
					.session(notExistsSession))
				.andExpect(status().isNotFound());

			validateErrorResponseWithErrorCode(actions, PayAccountErrorCode.ACCOUNT_NOT_FOUND);
		}

		@Test
		@DisplayName("요청 Amount가 Null인 경우 400을 return한다. 기존 잔고는 유지된다.")
		void nullAmountTest() throws Exception {
			//given
			PayAccountChargeRequest command = new PayAccountChargeRequest(null);

			//when & then
			ResultActions actions = mvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsBytes(command))
					.session(session))
				.andExpect(status().isBadRequest());

			verificationPersistedBalance(payAccount.getId(), originBalance);
		}

		@Test
		@DisplayName("요청 Amount가 음수인 경우 400을 return한다. 기존 잔고는 유지된다.")
		void negativeAmountTest() throws Exception {
			//given
			long amount = -1L;
			PayAccountChargeRequest command = new PayAccountChargeRequest(amount);

			//when & then
			ResultActions actions = mvc.perform(post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsBytes(command))
					.session(session))
				.andExpect(status().isBadRequest());

			validateErrorResponseWithErrorCode(actions, PayAccountErrorCode.INVALID_TRANSACTION_AMOUNT);
			verificationPersistedBalance(payAccount.getId(), originBalance);
		}
	}

	private ResultActions validateErrorResponseWithErrorCode(ResultActions actions, ErrorCode errorCode) throws
		Exception {
		return actions.andExpect(jsonPath("$.status").value(errorCode.getStatus()))
			.andExpect(jsonPath("$.detail").value(errorCode.getMessage()))
			.andExpect(jsonPath("$.errorCode").value(errorCode.getErrorCode()));
	}
}
