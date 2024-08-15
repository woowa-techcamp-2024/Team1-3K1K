package camp.woowak.lab.vendor.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.exception.PasswordMismatchException;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.vendor.service.command.SignInVendorCommand;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@Service
@Transactional(readOnly = true)
public class SignInVendorService {
	private final VendorRepository repository;
	private final PasswordEncoder passwordEncoder;

	public SignInVendorService(VendorRepository repository, PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * @throws PasswordMismatchException 비밀번호가 일치하지 않으면
	 */
	public UUID signIn(SignInVendorCommand cmd) {
		Vendor findVendor = repository.findByEmailOrThrow(cmd.email());
		if (!findVendor.matches(cmd.password(), passwordEncoder)) {
			throw new PasswordMismatchException();
		}
		return findVendor.getId();
	}
}
