package camp.woowak.lab.payaccount.domain;

import org.hibernate.annotations.ColumnDefault;

import camp.woowak.lab.payaccount.exception.InsufficientBalanceException;
import camp.woowak.lab.payaccount.exception.InvalidTransactionAmountException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PayAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "balance", nullable = false)
	@ColumnDefault("0")
	private long balance;

	public PayAccount() {
		this.balance = 0;
	}

	public Long getId() {
		return id;
	}

	public long getBalance() {
		return this.balance;
	}

	public PayAccountHistory withdraw(long amount) {
		validateTransactionAmount(amount);
		validateInsufficientBalance(amount);
		this.balance -= amount;

		return new PayAccountHistory(this, amount, AccountTransactionType.WITHDRAW);
	}

	public PayAccountHistory deposit(long amount) {
		validateTransactionAmount(amount);
		this.balance += amount;

		return new PayAccountHistory(this, amount, AccountTransactionType.DEPOSIT);
	}

	private void validateTransactionAmount(long amount) {
		if (amount <= 0)
			throw new InvalidTransactionAmountException("Transaction amount must be greater than zero.");
	}

	private void validateInsufficientBalance(long amount) {
		if (this.balance - amount < 0)
			throw new InsufficientBalanceException("Insufficient balance for this transaction.");
	}
}
