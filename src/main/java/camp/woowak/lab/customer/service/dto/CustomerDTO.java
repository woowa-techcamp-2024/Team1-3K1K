package camp.woowak.lab.customer.service.dto;

import java.util.UUID;

import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.payaccount.service.dto.PayAccountDTO;

public class CustomerDTO {
	private UUID id;
	private String name;
	private String email;
	private String phone;
	private PayAccountDTO payAccount;

	public CustomerDTO() {
	}

	public CustomerDTO(UUID id, String name, String email, String phone, PayAccountDTO payAccount) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.payAccount = payAccount;
	}

	public CustomerDTO(Customer customer) {
		this.id = customer.getId();
		this.name = customer.getName();
		this.email = customer.getEmail();
		this.phone = customer.getPhone();
		this.payAccount = new PayAccountDTO(customer.getPayAccount());
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public PayAccountDTO getPayAccount() {
		return payAccount;
	}
}

