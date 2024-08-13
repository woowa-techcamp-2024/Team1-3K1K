package camp.woowak.lab.web.api;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.payaccount.service.PayAccountChargeService;
import camp.woowak.lab.payaccount.service.command.PayAccountChargeCommand;
import camp.woowak.lab.web.dto.request.payaccount.PayAccountChargeRequest;
import jakarta.persistence.EntityManager;

@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("PayAccountApiController 클래스")
class PayAccountApiControllerTest {
	private final String BASE_URL = "/account/";
	@Autowired
	private MockMvc mvc;
	@Autowired
	private PayAccountRepository payAccountRepository;
	@Autowired
	private PayAccountChargeService payAccountChargeService;

	@Autowired
	private EntityManager em;

	@Autowired
	private ObjectMapper objectMapper;

	private PayAccount payAccount;
	private long originBalance;

	@BeforeEach
	void setUp() throws Exception {
		originBalance = 1000L;
		payAccount = new PayAccount();
		payAccount.deposit(originBalance);
		payAccountRepository.save(payAccount);
		em.detach(payAccount);
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

		@Test
		@DisplayName("존재하는 계정 ID에 정상범위의 금액을 입력하면 충전된다.")
		void successTest() throws Exception {
			//given
			long amount = 1000L;
			PayAccountChargeRequest command = new PayAccountChargeRequest(amount);

			//when & then
			mvc.perform(post(BASE_URL + payAccount.getId() + "/charge")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsBytes(command)))
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
			payAccountChargeService.chargeAccount(new PayAccountChargeCommand(payAccount.getId(), DAILY_LIMIT));
			PayAccountChargeRequest command = new PayAccountChargeRequest(amount);

			//when & then
			mvc.perform(post(BASE_URL + payAccount.getId() + "/charge")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsBytes(command)))
				.andExpect(status().isBadRequest());
		}

		//TODO : 아직 API Response Format이 정해지지 않았으므로, 논의 후 추가
		@Test
		@DisplayName("존재하지 않는 계정 ID를 입력하면 404를 return한다.")
		void notExistsAccountIdTest() throws Exception {
			//given
			long amount = 1000L;
			Long notExistsId = Long.MAX_VALUE;
			PayAccountChargeRequest command = new PayAccountChargeRequest(amount);

			//when & then
			mvc.perform(post(BASE_URL + notExistsId + "/charge")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsBytes(command)))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("요청 Amount가 Null인 경우 400을 return한다. 기존 잔고는 유지된다.")
		void nullAmountTest() throws Exception {
			//given
			PayAccountChargeRequest command = new PayAccountChargeRequest(null);

			//when & then
			mvc.perform(post(BASE_URL + payAccount.getId() + "/charge")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsBytes(command)))
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
			mvc.perform(post(BASE_URL + payAccount.getId() + "/charge")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsBytes(command)))
				.andExpect(status().isBadRequest());

			verificationPersistedBalance(payAccount.getId(), originBalance);
		}
	}
}
