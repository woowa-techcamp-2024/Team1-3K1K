package camp.woowak.lab.payaccount.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.exception.NotFoundAccountException;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.payaccount.service.command.AccountTransactionCommand;

@SpringBootTest
@DisplayName("PayAccountDepositService 클래스")
class PayAccountDepositServiceTest {
	@Autowired
	private PayAccountRepository payAccountRepository;

	@Autowired
	private PayAccountDepositService payAccountDepositService;

	private PayAccount payAccount;
	private final long originBalance = 1000L;

	@BeforeEach
	void setUp() throws Exception {
		payAccount = new PayAccount();
		payAccount.deposit(originBalance);
		payAccountRepository.save(payAccount);
		payAccountRepository.flush();
	}

	@Nested
	@DisplayName("Deposit 메서드는")
	class WithdrawAccount {
		@Test
		@DisplayName("AccountId와 Amount를 제공하면 잔고에 입금된다.")
		void withdrawAccount() {
			//given
			long amount = 100L;
			AccountTransactionCommand command = new AccountTransactionCommand(payAccount.getId(), amount);

			//when
			long afterBalance = payAccountDepositService.depositAccount(command);

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
			AccountTransactionCommand command = new AccountTransactionCommand(unknownAccountId, amount);

			//when & then
			assertThatThrownBy(() -> payAccountDepositService.depositAccount(command))
				.isExactlyInstanceOf(NotFoundAccountException.class);
			validateAfterBalanceInPersistenceLayer(payAccount.getId(), originBalance);
		}

		private void validateAfterBalanceInPersistenceLayer(Long accountId, long afterBalance) {
			Optional<PayAccount> byId = payAccountRepository.findById(accountId);
			assertThat(byId).isNotEmpty();
			PayAccount targetAccount = byId.get();
			assertThat(targetAccount.getBalance()).isEqualTo(afterBalance);
		}
	}
}