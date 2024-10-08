package camp.woowak.lab.vendor.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import camp.woowak.lab.fixture.VendorFixture;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.vendor.service.dto.VendorDTO;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RetrieveVendorServiceTest implements VendorFixture {
	@InjectMocks
	private RetrieveVendorService retrieveVendorService;

	@Mock
	private VendorRepository vendorRepository;

	@Test
	@DisplayName("전체 판매자 조회 테스트 - 성공")
	void testRetrieveVendors() {
		// given
		given(vendorRepository.findAll()).willReturn(
			List.of(createVendor(createPayAccount(), new NoOpPasswordEncoder())));
		// when
		List<VendorDTO> result = retrieveVendorService.retrieveVendors();
		// then
		assertEquals(1, result.size());
		verify(vendorRepository).findAll();
	}
}
