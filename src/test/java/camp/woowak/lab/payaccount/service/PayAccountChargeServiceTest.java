package camp.woowak.lab.payaccount.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.exception.DailyLimitExceededException;
import camp.woowak.lab.payaccount.exception.NotFoundAccountException;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.payaccount.service.command.PayAccountChargeCommand;
import jakarta.transaction.Transactional;

@SpringBootTest
@DisplayName("PayAccountChargeService 클래스")
@Transactional
class PayAccountChargeServiceTest {
	@Autowired
	private PayAccountRepository payAccountRepository;
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PayAccountChargeService payAccountChargeService;

	private Customer customer;
	private PayAccount payAccount;
	private final long originBalance = 1000L;

	@BeforeEach
	void setUp() throws Exception {
		payAccount = new PayAccount();
		payAccount.deposit(originBalance);
		payAccountRepository.saveAndFlush(payAccount);

		customer = new Customer(payAccount);
		customerRepository.saveAndFlush(customer);
	}

	@Nested
	@DisplayName("ChargeAccount 메서드는")
	class WithdrawAccount {
		@Test
		@DisplayName("AccountId와 Amount를 제공하면 잔고에 입금된다.")
		void withdrawAccount() {
			//given
			long amount = 100L;
			PayAccountChargeCommand command = new PayAccountChargeCommand(customer.getId(), amount);

			//when
			long afterBalance = payAccountChargeService.chargeAccount(command);

			//then
			assertThat(afterBalance).isEqualTo(originBalance + amount);
			validateAfterBalanceInPersistenceLayer(payAccount.getId(), afterBalance);
		}

		@Test
		@DisplayName("없는 AccountId를 호출하면 NotFoundAccountException을 던진다. 잔고는 유지된다.")
		void withdrawAccountNotFound() {
			//given
			Long unknownAccountId = Long.MAX_VALUE;
			long amount = 100L;
			PayAccountChargeCommand command = new PayAccountChargeCommand(unknownAccountId, amount);

			//when & then
			assertThatThrownBy(() -> payAccountChargeService.chargeAccount(command))
				.isExactlyInstanceOf(NotFoundAccountException.class);
			validateAfterBalanceInPersistenceLayer(payAccount.getId(), originBalance);
		}

		@Test
		@DisplayName("일일 한도 100만원을 초과해서 충전하면 DailyLimitExceededException을 던진다.")
		void dailyLimitExceeded() {
			//given
			long dailyLimit = 1_000_000L;
			long amount = 1000L;
			payAccountChargeService.chargeAccount(new PayAccountChargeCommand(customer.getId(), dailyLimit));
			PayAccountChargeCommand command = new PayAccountChargeCommand(customer.getId(), amount);

			//when & then
			assertThatThrownBy(() -> payAccountChargeService.chargeAccount(command))
				.isExactlyInstanceOf(DailyLimitExceededException.class);
		}

		private void validateAfterBalanceInPersistenceLayer(Long accountId, long afterBalance) {
			Optional<PayAccount> byId = payAccountRepository.findById(accountId);
			assertThat(byId).isNotEmpty();
			PayAccount targetAccount = byId.get();
			assertThat(targetAccount.getBalance()).isEqualTo(afterBalance);
		}
	}
}