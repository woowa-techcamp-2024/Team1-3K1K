package camp.woowak.lab.customer.domain;

import java.util.UUID;

import camp.woowak.lab.customer.exception.InvalidCreationException;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.web.authentication.PasswordEncoder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "Customers")
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@Column(nullable = false, length = 50)
	private String name;
	@Column(unique = true, nullable = false, length = 100)
	private String email;
	@Column(nullable = false, length = 30)
	private String password;
	@Column(nullable = false, length = 30)
	private String phone;
	@OneToOne(fetch = FetchType.LAZY)
	private PayAccount payAccount;

	public Customer() {
	}

	public Customer(String name, String email, String password, String phone, PayAccount payAccount,
					PasswordEncoder passwordEncoder) throws
		InvalidCreationException {
		CustomerValidator.validateCreation(name, email, password, phone, payAccount);
		this.name = name;
		this.email = email;
		this.password = passwordEncoder.encode(password);
		this.phone = phone;
		this.payAccount = payAccount;
	}

	public boolean validatePassword(String password, PasswordEncoder passwordEncoder) {
		return passwordEncoder.matches(password, this.password);
	}
}
