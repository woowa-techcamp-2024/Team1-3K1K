package camp.woowak.lab.customer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.customer.service.dto.CustomerDTO;
import camp.woowak.lab.fixture.CustomerFixture;

@ExtendWith(MockitoExtension.class)
class RetrieveCustomerServiceTest implements CustomerFixture {
	@InjectMocks
	private RetrieveCustomerService retrieveCustomerService;

	@Mock
	private CustomerRepository customerRepository;

	@Test
	@DisplayName("전체 Customer 조회 테스트 - 성공")
	void testRetrieveAllCustomers() {
		// given
		given(customerRepository.findAll()).willReturn(
			List.of(createCustomer(UUID.randomUUID()), createCustomer(UUID.randomUUID())));
		// when
		List<CustomerDTO> result = retrieveCustomerService.retrieveAllCustomers();

		// then
		assertEquals(2, result.size());
	}
}
