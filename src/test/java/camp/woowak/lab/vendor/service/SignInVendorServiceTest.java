package camp.woowak.lab.vendor.service;

import static org.mockito.BDDMockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import camp.woowak.lab.fixture.VendorFixture;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.exception.DuplicateEmailException;
import camp.woowak.lab.vendor.exception.NotFoundVendorException;
import camp.woowak.lab.vendor.exception.PasswordMismatchException;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.vendor.service.command.SignInVendorCommand;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class SignInVendorServiceTest implements VendorFixture {
	@InjectMocks
	private SignInVendorService service;
	@Mock
	private VendorRepository vendorRepository;
	@Mock
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("[성공] 비밀번호가 일치하면 id를 반환한다.")
	void success() throws DuplicateEmailException {
		// given
		PayAccount payAccount = createPayAccount();
		UUID fakeVendorId = UUID.randomUUID();
		Vendor vendor = createSavedVendor(fakeVendorId, payAccount, new NoOpPasswordEncoder());
		given(vendorRepository.findByEmailOrThrow(anyString())).willReturn(vendor);
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

		// when
		SignInVendorCommand command = new SignInVendorCommand("vendorEmail@email.com", "validPassword");
		UUID id = service.signIn(command);

		// then
		Assertions.assertEquals(vendor.getId(), id);
	}

	@Test
	@DisplayName("[예외] 존재하지 않는 Vendor면 NotFoundVendorException 발생")
	void failWithNotFound() throws DuplicateEmailException {
		// given
		given(vendorRepository.findByEmailOrThrow(anyString())).willThrow(NotFoundVendorException.class);

		// when
		SignInVendorCommand command = new SignInVendorCommand("notExists@email.com", "validPassword");

		// then
		Assertions.assertThrows(NotFoundVendorException.class, () -> service.signIn(command));
	}

	@Test
	@DisplayName("[예외] 비밀번호가 불일치하면 PasswordMismatchException 발생")
	void failWithPasswordMismatch() throws DuplicateEmailException {
		// given
		PayAccount payAccount = createPayAccount();
		UUID fakeVendorId = UUID.randomUUID();
		Vendor vendor = createSavedVendor(fakeVendorId, payAccount, new NoOpPasswordEncoder());
		given(vendorRepository.findByEmailOrThrow(anyString())).willReturn(vendor);
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

		// when
		SignInVendorCommand command = new SignInVendorCommand("vendorEmail@email.com", "validPassword");

		// then
		Assertions.assertThrows(PasswordMismatchException.class, () -> service.signIn(command));
	}
}