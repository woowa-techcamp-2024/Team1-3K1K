package camp.woowak.lab.payaccount.service.dto;

import camp.woowak.lab.payaccount.domain.PayAccount;

public class PayAccountDTO {
	private Long id;
	private Long balance;

	public PayAccountDTO() {
	}

	public PayAccountDTO(Long id, long balance) {
		this.id = id;
		this.balance = balance;
	}

	public PayAccountDTO(PayAccount payAccount) {
		this.id = payAccount.getId();
		this.balance = payAccount.getBalance();
	}

	public Long getId() {
		return id;
	}

	public Long getBalance() {
		return balance;
	}
}
