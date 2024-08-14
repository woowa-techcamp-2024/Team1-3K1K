package camp.woowak.lab.payaccount.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import camp.woowak.lab.payaccount.exception.DailyLimitExceededException;
import camp.woowak.lab.payaccount.exception.InsufficientBalanceException;
import camp.woowak.lab.payaccount.exception.InvalidTransactionAmountException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
public class PayAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "balance", nullable = false)
	@ColumnDefault("0")
	private long balance;

	@OneToMany(mappedBy = "payAccount", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private List<PayAccountHistory> history = new ArrayList<>();

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

		return issueAndSavePayAccountHistory(amount, AccountTransactionType.WITHDRAW);
	}

	public PayAccountHistory deposit(long amount) {
		validateTransactionAmount(amount);
		this.balance += amount;

		return issueAndSavePayAccountHistory(amount, AccountTransactionType.DEPOSIT);
	}

	public PayAccountHistory charge(long amount) {
		validateTransactionAmount(amount);
		validateDailyChargeLimit(amount);
		this.balance += amount;

		return issueAndSavePayAccountHistory(amount, AccountTransactionType.CHARGE);
	}

	private PayAccountHistory issueAndSavePayAccountHistory(long amount, AccountTransactionType type) {
		PayAccountHistory payAccountHistory = new PayAccountHistory(this, amount, type);
		this.history.add(payAccountHistory);

		return payAccountHistory;
	}

	private void validateDailyChargeLimit(long amount) {
		LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1);

		long todayTotalCharge = history.stream()
			.filter(h -> h.getType() == AccountTransactionType.CHARGE)
			.filter(h -> h.getCreatedAt().isAfter(startOfDay) && h.getCreatedAt().isBefore(endOfDay))
			.mapToLong(PayAccountHistory::getAmount)
			.sum();

		if (todayTotalCharge + amount > 1_000_000) {
			log.warn("Daily charge limit of {} exceeded.", 1_000_000);
			throw new DailyLimitExceededException();
		}
	}

	private void validateTransactionAmount(long amount) {
		if (amount <= 0) {
			log.warn("Transaction amount must be greater than zero.");
			throw new InvalidTransactionAmountException();
		}
	}

	private void validateInsufficientBalance(long amount) {
		if (this.balance - amount < 0) {
			log.warn("Insufficient balance for this transaction.");
			throw new InsufficientBalanceException();
		}
	}
}
