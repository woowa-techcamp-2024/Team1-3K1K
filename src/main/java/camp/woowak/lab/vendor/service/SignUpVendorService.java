package camp.woowak.lab.vendor.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.exception.DuplicateEmailException;
import camp.woowak.lab.vendor.exception.InvalidCreationException;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.vendor.service.command.SignUpVendorCommand;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@Service
@Transactional(rollbackFor = {InvalidCreationException.class, DuplicateEmailException.class})
public class SignUpVendorService {
	private final VendorRepository vendorRepository;
	private final PayAccountRepository payAccountRepository;
	private final PasswordEncoder passwordEncoder;

	public SignUpVendorService(
		VendorRepository vendorRepository, PayAccountRepository payAccountRepository, PasswordEncoder passwordEncoder) {
		this.vendorRepository = vendorRepository;
		this.payAccountRepository = payAccountRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public Long signUp(SignUpVendorCommand cmd) throws InvalidCreationException, DuplicateEmailException {
		PayAccount newPayAccount = new PayAccount();
		payAccountRepository.save(newPayAccount);
		Vendor newVendor =
			new Vendor(cmd.name(), cmd.email(), cmd.password(), cmd.phone(), newPayAccount, passwordEncoder);
		try {
			vendorRepository.save(newVendor);
		} catch (DataIntegrityViolationException e) {
			throw new DuplicateEmailException();
		}
		return newVendor.getId();
	}
}
