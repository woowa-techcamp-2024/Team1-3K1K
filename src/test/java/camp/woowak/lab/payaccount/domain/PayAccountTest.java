package camp.woowak.lab.payaccount.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.payaccount.exception.InsufficientBalanceException;
import camp.woowak.lab.payaccount.exception.InvalidTransactionAmountException;

@DisplayName("PayAccount 클래스")
class PayAccountTest {
	private PayAccount payAccount;

	@BeforeEach
	void setUp() {
		payAccount = new PayAccount();
	}

	@Nested
	@DisplayName("기본 생성자는")
	class DefaultConstructorTest {
		@Test
		@DisplayName("기본 생성자로 호출하면 balance는 0이된다.")
		void initializeBalanceToZero() {
			// then
			assertThat(payAccount.getBalance()).isZero();
		}
	}

	@Nested
	@DisplayName("Deposit 메서드는")
	class DepositTest {
		@Test
		@DisplayName("입금한 만큼 잔고가 증가한다.")
		void increaseBalanceByDepositedAmount() {
			// given
			long amount = 1000;

			// when
			PayAccountHistory depositHistory = payAccount.deposit(amount);

			// then
			assertThat(payAccount.getBalance()).isEqualTo(amount);
			assertThat(depositHistory.getAmount()).isEqualTo(amount);
			assertThat(depositHistory.getType()).isEqualTo(AccountTransactionType.DEPOSIT);
		}

		@Test
		@DisplayName("음수를 입금하려하면 exception을 던진다. 잔고는 유지된다.")
		void throwExceptionForNegativeAmount() {
			// given
			long amount = -1000;

			// when & then
			assertThatThrownBy(() -> payAccount.deposit(amount))
				.isExactlyInstanceOf(InvalidTransactionAmountException.class);
			assertThat(payAccount.getBalance()).isZero();
		}

		@Test
		@DisplayName("0원을 입금하려면 exception을 던진다. 잔고는 유지된다.")
		void throwExceptionForZeroAmount() {
			// given
			long amount = 0;

			// when & then
			assertThatThrownBy(() -> payAccount.deposit(amount))
				.isExactlyInstanceOf(InvalidTransactionAmountException.class);
			assertThat(payAccount.getBalance()).isZero();
		}
	}

	@Nested
	@DisplayName("Withdraw 메서드는")
	class WithdrawTest {
		private long originBalance = 1000;

		@BeforeEach
		void setUpBalance() {
			payAccount.deposit(originBalance);
		}

		@Test
		@DisplayName("출금한 만큼 잔고에서 차감된다.")
		void decreaseBalanceByWithdrawnAmount() {
			// given
			long withdrawAmount = 100;

			// when
			PayAccountHistory withdrawHistory = payAccount.withdraw(withdrawAmount);

			// then
			assertThat(payAccount.getBalance()).isEqualTo(originBalance - withdrawAmount);
			assertThat(withdrawHistory.getAmount()).isEqualTo(withdrawAmount);
			assertThat(withdrawHistory.getType()).isEqualTo(AccountTransactionType.WITHDRAW);
		}

		@Test
		@DisplayName("음수를 출금하려면 exception을 던진다. 잔고는 유지된다.")
		void throwExceptionForNegativeAmount() {
			// given
			long amount = -100;

			// when & then
			assertThatThrownBy(() -> payAccount.withdraw(amount))
				.isExactlyInstanceOf(InvalidTransactionAmountException.class);
			assertThat(payAccount.getBalance()).isEqualTo(originBalance);
		}

		@Test
		@DisplayName("0원을 출금하려면 exception을 던진다. 잔고는 유지된다.")
		void throwExceptionForZeroAmount() {
			// given
			long amount = 0;

			// when & then
			assertThatThrownBy(() -> payAccount.withdraw(amount))
				.isExactlyInstanceOf(InvalidTransactionAmountException.class);
			assertThat(payAccount.getBalance()).isEqualTo(originBalance);
		}

		@Test
		@DisplayName("남은 잔고보다 더 많은 돈을 출금하려면 exception을 던진다. 잔고는 유지된다.")
		void throwExceptionForInsufficientBalance() {
			// given
			long amount = originBalance + 1;

			// when & then
			assertThatThrownBy(() -> payAccount.withdraw(amount))
				.isExactlyInstanceOf(InsufficientBalanceException.class);
			assertThat(payAccount.getBalance()).isEqualTo(originBalance);
		}
	}
}