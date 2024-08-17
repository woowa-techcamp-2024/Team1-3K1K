package camp.woowak.lab.vendor.service.dto;

import java.util.UUID;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.vendor.domain.Vendor;
import lombok.Getter;

@Getter
public class VendorDTO {
	private final UUID id;
	private final String name;
	private final String email;
	private final String phone;
	private final PayAccountDTO payAccount;

	public VendorDTO(Vendor vendor) {
		this.id = vendor.getId();
		this.name = vendor.getName();
		this.email = vendor.getEmail();
		this.phone = vendor.getPhone();
		this.payAccount = new PayAccountDTO(vendor.getPayAccount());
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
