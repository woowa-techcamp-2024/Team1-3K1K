package camp.woowak.lab.vendor.service.dto;

import java.util.UUID;

import camp.woowak.lab.payaccount.service.dto.PayAccountDTO;
import camp.woowak.lab.vendor.domain.Vendor;

public class VendorDTO {
	private UUID id;
	private String name;
	private String email;
	private String phone;
	private PayAccountDTO payAccount;

	public VendorDTO() {
	}

	public VendorDTO(UUID id, String name, String email, String phone, PayAccountDTO payAccount) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.payAccount = payAccount;
	}

	public VendorDTO(Vendor vendor) {
		this.id = vendor.getId();
		this.name = vendor.getName();
		this.email = vendor.getEmail();
		this.phone = vendor.getPhone();
		this.payAccount = new PayAccountDTO(vendor.getPayAccount());
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
