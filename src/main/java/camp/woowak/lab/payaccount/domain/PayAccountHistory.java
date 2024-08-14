package camp.woowak.lab.payaccount.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@EntityListeners(value = AuditingEntityListener.class)
public class PayAccountHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	private PayAccount payAccount;

	@Column
	private long amount;

	@Enumerated(value = EnumType.STRING)
	private AccountTransactionType type;

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	public PayAccountHistory() {
	}

	public PayAccountHistory(PayAccount payAccount, long amount, AccountTransactionType type) {
		this.payAccount = payAccount;
		this.amount = amount;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public long getAmount() {
		return amount;
	}

	public AccountTransactionType getType() {
		return type;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
