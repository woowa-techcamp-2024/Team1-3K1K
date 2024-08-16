package camp.woowak.lab.coupon.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import camp.woowak.lab.coupon.domain.Coupon;
import camp.woowak.lab.coupon.domain.CouponIssuance;
import camp.woowak.lab.coupon.exception.InsufficientCouponQuantityException;
import camp.woowak.lab.coupon.repository.CouponIssuanceRepository;
import camp.woowak.lab.coupon.repository.CouponRepository;
import camp.woowak.lab.coupon.service.command.IssueCouponCommand;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.fixture.CouponFixture;
import camp.woowak.lab.fixture.CustomerFixture;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import jakarta.transaction.Transactional;

@SpringBootTest
class IssueCouponServiceIntegrationTest implements CouponFixture, CustomerFixture {
	@Autowired
	private IssueCouponService service;

	@Autowired
	private CouponIssuanceRepository couponIssuanceRepository;

	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PayAccountRepository payAccountRepository;

	@Test
	@DisplayName("쿠폰 발급 테스트 - 성공")
	@Transactional
	void testIssueCoupon() {
		// given
		int initialQuantity = 100;
		Coupon coupon = createCoupon("할인 쿠폰", 1000, initialQuantity, LocalDateTime.now().plusDays(7));
		// 쿠폰 등록
		Long couponId = couponRepository.save(coupon).getId();

		// 고객 등록
		// 계좌 생성
		PayAccount payAccount = payAccountRepository.save(createPayAccount());
		Customer customer = createCustomer(payAccount, new NoOpPasswordEncoder());
		UUID customerId = customerRepository.saveAndFlush(customer).getId();
		IssueCouponCommand cmd = new IssueCouponCommand(customerId, couponId);

		// when
		Long saveCouponIssuanceId = service.issueCoupon(cmd);
		CouponIssuance couponIssuance = couponIssuanceRepository.findById(saveCouponIssuanceId).get();

		// then
		// 쿠폰 발급 확인
		assertNotNull(saveCouponIssuanceId);
		assertEquals(coupon.getId(), couponIssuance.getCoupon().getId());
		assertEquals(customer.getId(), couponIssuance.getCustomer().getId());
		assertEquals(initialQuantity - 1, couponIssuance.getCoupon().getQuantity());
	}

	@Test
	@DisplayName("쿠폰 발급 테스트 - 수량 부족 실패")
	@Transactional
	void testIssueCouponFailWithInsufficientQuantity() {
		// given
		Coupon coupon = createCoupon("할인 쿠폰", 1000, 1, LocalDateTime.now().plusDays(7));
		coupon.decreaseQuantity();
		// 쿠폰 등록
		Long couponId = couponRepository.save(coupon).getId();

		// 고객 등록
		// 계좌 생성
		PayAccount payAccount = payAccountRepository.save(createPayAccount());
		Customer customer = createCustomer(payAccount, new NoOpPasswordEncoder());
		UUID customerId = customerRepository.saveAndFlush(customer).getId();
		IssueCouponCommand cmd = new IssueCouponCommand(customerId, couponId);

		// when & then
		assertThrows(InsufficientCouponQuantityException.class,
			() -> service.issueCoupon(cmd));
	}

	@Test
	@DisplayName("쿠폰 발급 테스트 - 동시성 제어")
	void testIssueCouponWithConcurrency() throws InterruptedException {
		// given
		int couponQuantity = 10;
		int numberOfThreads = 20;
		Coupon coupon = createCoupon("할인 쿠폰", 1000, couponQuantity, LocalDateTime.now().plusDays(7));
		Long couponId = couponRepository.save(coupon).getId();

		PayAccount payAccount = payAccountRepository.save(createPayAccount());
		Customer customer = createCustomer(payAccount, new NoOpPasswordEncoder());
		UUID customerId = customerRepository.saveAndFlush(customer).getId();

		IssueCouponCommand cmd = new IssueCouponCommand(customerId, couponId);

		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failCount = new AtomicInteger(0);
		List<Exception> exceptions = new ArrayList<>();

		// when
		for (int i = 0; i < numberOfThreads; i++) {
			executorService.submit(() -> {
				try {
					service.issueCoupon(cmd);
					successCount.incrementAndGet();
				} catch (InsufficientCouponQuantityException e) {
					failCount.incrementAndGet();
				} catch (Exception e) {
					exceptions.add(e);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
		executorService.shutdown();

		// then
		assertEquals(couponQuantity, successCount.get(), "성공적으로 발급된 쿠폰 수가 초기 수량과 일치해야 합니다.");
		assertEquals(numberOfThreads - couponQuantity, failCount.get(), "실패한 요청 수가 예상과 일치해야 합니다.");
		assertTrue(exceptions.isEmpty(), "예상치 못한 예외가 발생하지 않아야 합니다.");

		Coupon updatedCoupon = couponRepository.findById(couponId).orElseThrow();
		assertEquals(0, updatedCoupon.getQuantity(), "모든 쿠폰이 소진되어야 합니다.");

		// 쓰레드 트랜잭션 전파 문제로 인해 수동 데이터 제거
		// 데이터 제거
		couponIssuanceRepository.deleteAll();
		couponRepository.delete(updatedCoupon);
		customerRepository.delete(customer);
		payAccountRepository.delete(payAccount);
	}
}
