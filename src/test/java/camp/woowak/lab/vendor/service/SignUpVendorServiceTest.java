package camp.woowak.lab.vendor.service;

import static org.mockito.BDDMockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import camp.woowak.lab.fixture.VendorFixture;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.exception.DuplicateEmailException;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.vendor.service.command.SignUpVendorCommand;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class SignUpVendorServiceTest implements VendorFixture {
	@InjectMocks
	private SignUpVendorService service;
	@Mock
	private VendorRepository vendorRepository;
	@Mock
	private PayAccountRepository payAccountRepository;
	@Mock
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("[성공] Vendor가 저장된다.")
	void success() throws DuplicateEmailException {
		// given
		PayAccount payAccount = createPayAccount();
		UUID fakeVendorId = UUID.randomUUID();
		Vendor vendor = createSavedVendor(fakeVendorId, payAccount, new NoOpPasswordEncoder());
		given(passwordEncoder.encode(Mockito.anyString())).willReturn("password");
		given(payAccountRepository.save(Mockito.any(PayAccount.class))).willReturn(payAccount);
		given(vendorRepository.save(Mockito.any(Vendor.class))).willReturn(vendor);

		// when
		SignUpVendorCommand command =
			new SignUpVendorCommand("vendorName", "vendorEmail@example.com", "password", "010-0000-0000");
		service.signUp(command);

		// then
		then(payAccountRepository).should().save(Mockito.any(PayAccount.class));
		then(vendorRepository).should().save(Mockito.any(Vendor.class));
	}

	@Test
	@DisplayName("[예외] 가입된 이메일인 경우 예외 발생")
	void failWithDuplicateEmail() throws DuplicateEmailException {
		// given
		given(passwordEncoder.encode(Mockito.anyString())).willReturn("password");
		given(payAccountRepository.save(Mockito.any(PayAccount.class))).willReturn(createPayAccount());

		// when
		when(vendorRepository.save(Mockito.any(Vendor.class))).thenThrow(DataIntegrityViolationException.class);
		SignUpVendorCommand command =
			new SignUpVendorCommand("vendorName", "vendorEmail@example.com", "password", "010-0000-0000");

		// then
		Assertions.assertThrows(DuplicateEmailException.class, () -> service.signUp(command));
		then(payAccountRepository).should().save(Mockito.any(PayAccount.class));
		then(vendorRepository).should().save(Mockito.any(Vendor.class));
	}
}
