package camp.woowak.lab.coupon.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import camp.woowak.lab.coupon.domain.Coupon;
import camp.woowak.lab.coupon.domain.CouponIssuance;
import camp.woowak.lab.coupon.repository.CouponIssuanceRepository;
import camp.woowak.lab.coupon.repository.CouponRepository;
import camp.woowak.lab.coupon.service.command.IssueCouponCommand;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.fixture.CouponFixture;
import camp.woowak.lab.fixture.CouponIssuanceFixture;
import camp.woowak.lab.fixture.CustomerFixture;

@ExtendWith(MockitoExtension.class)
class IssueCouponServiceTest implements CouponFixture, CustomerFixture, CouponIssuanceFixture {
	@InjectMocks
	private IssueCouponService issueCouponService;

	@Mock
	private CouponIssuanceRepository couponIssuanceRepository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private CouponRepository couponRepository;

	@Test
	@DisplayName("Coupon 발급 테스트 - 성공")
	void testIssueCoupon() {
		// given
		UUID fakeCustomerId = UUID.randomUUID();
		Long fakeCouponId = 1L;
		Long fakeCouponIssuanceId = 1L;
		Coupon fakeCoupon = createCoupon(fakeCouponId, "할인 쿠폰", 1000, 100, LocalDateTime.now().plusDays(7));
		Customer fakeCustomer = createCustomer(fakeCustomerId);
		CouponIssuance fakeCouponIssuance = createCouponIssuance(fakeCouponId, fakeCoupon, fakeCustomer);
		given(customerRepository.findById(fakeCustomerId)).willReturn(Optional.of(fakeCustomer));
		given(couponRepository.findById(fakeCouponId)).willReturn(Optional.of(fakeCoupon));
		given(couponIssuanceRepository.save(any(CouponIssuance.class))).willReturn(fakeCouponIssuance);

		IssueCouponCommand cmd = new IssueCouponCommand(fakeCustomerId, fakeCouponId);

		// when
		Long saveId = issueCouponService.issueCoupon(cmd);

		// then
		assertEquals(fakeCouponIssuanceId, saveId);
		verify(customerRepository).findById(fakeCustomerId);
		verify(couponRepository).findById(fakeCouponId);
		verify(couponIssuanceRepository).save(any(CouponIssuance.class));
	}
}
