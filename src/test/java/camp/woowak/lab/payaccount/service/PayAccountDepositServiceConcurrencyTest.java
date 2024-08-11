package camp.woowak.lab.payaccount.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.payaccount.service.command.AccountTransactionCommand;

@SpringBootTest
@DisplayName("PayAccountWithdrawService 클래스")
class PayAccountDepositServiceConcurrencyTest {
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
	@DisplayName("withdrawAccount 메서드는")
	class WithdrawAccount {
		@Test
		@DisplayName("동시에 여러 오청이 들어오면 요청에 맞게 출금이 모두 완료되어야한다.")
		void withdrawAccountWithdrawMultipleRequest() throws InterruptedException {
			//given
			int multipleRequestCount = 100;
			long eachAmount = 1L;
			AccountTransactionCommand command = new AccountTransactionCommand(payAccount.getId(), eachAmount);

			ExecutorService executorService = Executors.newFixedThreadPool(multipleRequestCount);
			CountDownLatch latch = new CountDownLatch(multipleRequestCount);

			//when
			IntStream.range(0, multipleRequestCount)
				.forEach(i -> {
					executorService.submit(() -> {
						payAccountDepositService.depositAccount(command);
						latch.countDown();
					});
				});

			latch.await();

			//then
			validateAfterBalanceInPersistenceLayer(payAccount.getId(),
				originBalance + (multipleRequestCount * eachAmount));
		}

		private void validateAfterBalanceInPersistenceLayer(Long accountId, long afterBalance) {
			Optional<PayAccount> byId = payAccountRepository.findById(accountId);
			assertThat(byId).isNotEmpty();
			PayAccount targetAccount = byId.get();
			assertThat(targetAccount.getBalance()).isEqualTo(afterBalance);
		}
	}
}