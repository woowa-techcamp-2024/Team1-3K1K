package camp.woowak.lab.customer.service.dto;

import java.util.UUID;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.payaccount.domain.PayAccount;
import lombok.Getter;

@Getter
public class CustomerDTO {
	private final UUID id;
	private final String name;
	private final String email;
	private final String phone;
	private final PayAccountDTO payAccount;

	public CustomerDTO(Customer customer) {
		this.id = customer.getId();
		this.name = customer.getName();
		this.email = customer.getEmail();
		this.phone = customer.getPhone();
		this.payAccount = new PayAccountDTO(customer.getPayAccount());
	}

	@Getter
	public static class PayAccountDTO {
		private final Long id;
		private final Long balance;

		public PayAccountDTO(PayAccount payAccount) {
			this.id = payAccount.getId();
			this.balance = payAccount.getBalance();
		}
	}
}

