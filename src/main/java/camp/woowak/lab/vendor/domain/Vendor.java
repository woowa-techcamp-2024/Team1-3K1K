package camp.woowak.lab.vendor.domain;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.vendor.exception.InvalidCreationException;
import camp.woowak.lab.web.authentication.PasswordEncoder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Vendor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
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

	protected Vendor() {
	}

	public Vendor(String name, String email, String password, String phone, PayAccount payAccount,
		PasswordEncoder passwordEncoder) throws InvalidCreationException {
		checkName(name);
		checkEmail(email);
		checkPassword(password);
		checkPhone(phone);
		checkPayAccount(payAccount);
		this.name = name;
		this.email = email;
		this.password = passwordEncoder.encode(password);
		this.phone = phone;
		this.payAccount = payAccount;
	}

	public Long getId() {
		return id;
	}

	private void checkPayAccount(PayAccount payAccount) throws InvalidCreationException {
		if (payAccount == null) {
			throw new InvalidCreationException("Pay account cannot be null");
		}
	}

	private void checkPhone(String phone) throws InvalidCreationException {
		if (phone == null || phone.isBlank()) {
			throw new InvalidCreationException("Vendor phone cannot be blank");
		}
		if (phone.trim().length() > 30) {
			throw new InvalidCreationException("Vendor phone cannot be longer than 30 characters");
		}
	}

	private void checkPassword(String password) throws InvalidCreationException {
		if (password == null || password.isBlank()) {
			throw new InvalidCreationException("Vendor password cannot be blank");
		}
		if (password.trim().length() < 8 || password.trim().length() > 30) {
			throw new InvalidCreationException(
				"Vendor password must be at least 8 characters and at most 30 characters");
		}
	}

	private void checkEmail(String email) throws InvalidCreationException {
		if (email == null || email.isBlank()) {
			throw new InvalidCreationException("Vendor email cannot be blank");
		}
		if (email.trim().length() > 100) {
			throw new InvalidCreationException("Vendor email cannot be longer than 100 characters");
		}
	}

	private void checkName(String name) throws InvalidCreationException {
		if (name == null || name.isBlank()) {
			throw new InvalidCreationException("Vendor name cannot be blank");
		}
		if (name.length() > 50) {
			throw new InvalidCreationException("Vendor name cannot exceed 50 characters");
		}
	}
}
