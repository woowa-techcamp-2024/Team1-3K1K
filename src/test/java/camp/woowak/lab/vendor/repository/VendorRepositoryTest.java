package camp.woowak.lab.vendor.repository;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;

import camp.woowak.lab.fixture.VendorFixture;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

@DataJpaTest
class VendorRepositoryTest implements VendorFixture {
	@Autowired
	private VendorRepository vendorRepository;
	@Autowired
	private PayAccountRepository payAccountRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@TestConfiguration
	static class TestContextConfiguration {
		@Bean
		public PasswordEncoder passwordEncoder() {
			return new NoOpPasswordEncoder();
		}
	}

	@AfterEach
	void tearDown() {
		vendorRepository.deleteAll();
	}

	@Nested
	@DisplayName("Vendor 저장은")
	class IsSaved {
		@Test
		@DisplayName("[성공] DB에 저장된다.")
		void success() {
			// given
			PayAccount payAccount = payAccountRepository.save(createPayAccount());

			// when
			Vendor vendor = createVendor(payAccount, passwordEncoder);
			Vendor savedVendor = vendorRepository.save(vendor);
			UUID savedVendorId = savedVendor.getId();
			vendorRepository.flush();

			// then
			Optional<Vendor> findVendor = vendorRepository.findById(savedVendorId);
			Assertions.assertTrue(findVendor.isPresent());
			Assertions.assertEquals(savedVendorId, findVendor.get().getId());
		}

		@Test
		@DisplayName("[예외] 중복된 이메일이 있으면 예외가 발생한다.")
		void failWithDuplicateEmail() {
			// given
			PayAccount payAccount = payAccountRepository.save(createPayAccount());
			Vendor vendor = createVendor(payAccount, passwordEncoder);
			vendorRepository.saveAndFlush(vendor);

			// when
			PayAccount newPayAccount = payAccountRepository.save(createPayAccount());
			Vendor newVendor = createVendor(newPayAccount, passwordEncoder);

			// then
			Assertions.assertThrows(DataIntegrityViolationException.class, () -> vendorRepository.save(newVendor));
		}
	}
}
