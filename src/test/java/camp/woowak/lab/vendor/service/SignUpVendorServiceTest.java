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

		// when
		when(payAccountRepository.save(Mockito.any(PayAccount.class))).thenReturn(payAccount);
		when(vendorRepository.saveAndFlush(Mockito.any(Vendor.class))).thenReturn(vendor);

		// then
		SignUpVendorCommand command =
			new SignUpVendorCommand("vendorName", "vendorEmail@example.com", "password", "010-0000-0000");
		service.signUp(command);
		then(payAccountRepository).should().save(Mockito.any(PayAccount.class));
		then(vendorRepository).should().saveAndFlush(Mockito.any(Vendor.class));
	}

	@Test
	@DisplayName("[예외] 가입된 이메일인 경우 예외 발생")
	void failWithDuplicateEmail() throws DuplicateEmailException {
		// given
		given(passwordEncoder.encode(Mockito.anyString())).willReturn("password");

		// when
		when(payAccountRepository.save(Mockito.any(PayAccount.class))).thenReturn(createPayAccount());
		when(vendorRepository.saveAndFlush(Mockito.any(Vendor.class))).thenThrow(DataIntegrityViolationException.class);

		// then
		SignUpVendorCommand command =
			new SignUpVendorCommand("vendorName", "vendorEmail@example.com", "password", "010-0000-0000");
		Assertions.assertThrows(DuplicateEmailException.class, () -> service.signUp(command));
		then(payAccountRepository).should().save(Mockito.any(PayAccount.class));
		then(vendorRepository).should().saveAndFlush(Mockito.any(Vendor.class));
	}
}
