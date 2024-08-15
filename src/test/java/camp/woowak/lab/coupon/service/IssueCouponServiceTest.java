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
import camp.woowak.lab.coupon.domain.TestCoupon;
import camp.woowak.lab.coupon.exception.ExpiredCouponException;
import camp.woowak.lab.coupon.exception.InsufficientCouponQuantityException;
import camp.woowak.lab.coupon.exception.InvalidICreationIssuanceException;
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

	@Test
	@DisplayName("Coupon 발급 테스트 - 존재하지 않는 Customer")
	void testIssueCouponFailWithNotExistCustomer() {
		// given
		UUID fakeCustomerId = UUID.randomUUID();
		Long fakeCouponId = 1L;
		given(customerRepository.findById(fakeCustomerId)).willReturn(Optional.empty());

		IssueCouponCommand cmd = new IssueCouponCommand(fakeCustomerId, fakeCouponId);

		// when & then
		assertThrows(InvalidICreationIssuanceException.class, () -> issueCouponService.issueCoupon(cmd));
		verify(customerRepository).findById(fakeCustomerId);
		verify(couponRepository, never()).findById(fakeCouponId);
		verify(couponIssuanceRepository, never()).save(any(CouponIssuance.class));
	}

	@Test
	@DisplayName("Coupon 발급 테스트 - 존재하지 않는 Coupon")
	void testIssueCouponFailWithNotExistCoupon() {
		// given
		UUID fakeCustomerId = UUID.randomUUID();
		Long fakeCouponId = 1L;
		Customer fakeCustomer = createCustomer(fakeCustomerId);
		given(customerRepository.findById(fakeCustomerId)).willReturn(Optional.of(fakeCustomer));
		given(couponRepository.findById(fakeCouponId)).willReturn(Optional.empty());

		IssueCouponCommand cmd = new IssueCouponCommand(fakeCustomerId, fakeCouponId);

		// when & then
		assertThrows(InvalidICreationIssuanceException.class, () -> issueCouponService.issueCoupon(cmd));
		verify(customerRepository).findById(fakeCustomerId);
		verify(couponRepository).findById(fakeCouponId);
		verify(couponIssuanceRepository, never()).save(any(CouponIssuance.class));
	}

	@Test
	@DisplayName("Coupon 발급 테스트 - 만료된 Coupon")
	void testIssueCouponFailWithExpiredCoupon() {
		// given
		UUID fakeCustomerId = UUID.randomUUID();
		Long fakeCouponId = 1L;
		TestCoupon fakeCoupon = (TestCoupon)createCoupon(fakeCouponId, "할인 쿠폰", 1000, 100,
			LocalDateTime.now().plusDays(7));
		fakeCoupon.setExpiredAt(LocalDateTime.now().minusDays(1));
		Customer fakeCustomer = createCustomer(fakeCustomerId);
		given(customerRepository.findById(fakeCustomerId)).willReturn(Optional.of(fakeCustomer));
		given(couponRepository.findById(fakeCouponId)).willReturn(Optional.of(fakeCoupon));

		IssueCouponCommand cmd = new IssueCouponCommand(fakeCustomerId, fakeCouponId);

		// when & then
		assertThrows(ExpiredCouponException.class, () -> issueCouponService.issueCoupon(cmd));
		verify(customerRepository).findById(fakeCustomerId);
		verify(couponRepository).findById(fakeCouponId);
		verify(couponIssuanceRepository, never()).save(any(CouponIssuance.class));
	}

	@Test
	@DisplayName("Coupon 발급 테스트 - 수량 부족")
	void testIssueCouponFailWithInsufficientCouponQuantity() {
		// given
		UUID fakeCustomerId = UUID.randomUUID();
		Long fakeCouponId = 1L;
		TestCoupon fakeCoupon = (TestCoupon)createCoupon(fakeCouponId, "할인 쿠폰", 1000, 1,
			LocalDateTime.now().plusDays(7));
		Customer fakeCustomer = createCustomer(fakeCustomerId);
		fakeCoupon.setQuantity(0); // 수량 부족 시나리오 적용
		given(customerRepository.findById(fakeCustomerId)).willReturn(Optional.of(fakeCustomer));
		given(couponRepository.findById(fakeCouponId)).willReturn(Optional.of(fakeCoupon));
		given(couponIssuanceRepository.save(any(CouponIssuance.class))).willThrow(
			new InsufficientCouponQuantityException("the quantity of coupon is insufficient"));
		IssueCouponCommand cmd = new IssueCouponCommand(fakeCustomerId, fakeCouponId);

		// when & then
		assertThrows(InsufficientCouponQuantityException.class, () -> issueCouponService.issueCoupon(cmd));
		verify(customerRepository).findById(fakeCustomerId);
		verify(couponRepository).findById(fakeCouponId);
		verify(couponIssuanceRepository).save(any(CouponIssuance.class));
	}
}
