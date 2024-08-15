package camp.woowak.lab.coupon.service;

import org.springframework.stereotype.Service;

import camp.woowak.lab.coupon.domain.Coupon;
import camp.woowak.lab.coupon.domain.CouponIssuance;
import camp.woowak.lab.coupon.exception.ExpiredCouponException;
import camp.woowak.lab.coupon.exception.InsufficientCouponQuantityException;
import camp.woowak.lab.coupon.exception.InvalidICreationIssuanceException;
import camp.woowak.lab.coupon.repository.CouponIssuanceRepository;
import camp.woowak.lab.coupon.repository.CouponRepository;
import camp.woowak.lab.coupon.service.command.IssueCouponCommand;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import jakarta.transaction.Transactional;

@Service
public class IssueCouponService {
	private final CouponIssuanceRepository couponIssuanceRepository;
	private final CouponRepository couponRepository;
	private final CustomerRepository customerRepository;

	public IssueCouponService(CouponIssuanceRepository couponIssuanceRepository, CouponRepository couponRepository,
							  CustomerRepository customerRepository) {
		this.couponIssuanceRepository = couponIssuanceRepository;
		this.couponRepository = couponRepository;
		this.customerRepository = customerRepository;
	}

	/**
	 *
	 * @throws InvalidICreationIssuanceException customer 또는 coupon이 존재하지 않을 경우 또는 coupon이 만료되었을 경우
	 * @throws ExpiredCouponException coupon이 만료되었을 경우
	 * @throws InsufficientCouponQuantityException coupon 수량이 부족할 경우
	 */
	@Transactional
	public Long issueCoupon(IssueCouponCommand cmd) {
		// customer 조회
		Customer targetCustomer = customerRepository.findById(cmd.customerId())
			.orElseThrow(() -> new InvalidICreationIssuanceException("customer not found"));

		// coupon 조회
		Coupon targetCoupon = couponRepository.findByIdWithPessimisticLock(cmd.couponId())
			.orElseThrow(() -> new InvalidICreationIssuanceException("coupon not found"));

		// coupon 수량 확인
		if (!targetCoupon.hasAvailableQuantity()) {
			throw new InsufficientCouponQuantityException("quantity of coupon is insufficient");
		}

		// coupon issuance 생성
		CouponIssuance newCouponIssuance = new CouponIssuance(targetCoupon, targetCustomer);

		// coupon issuance 저장
		return couponIssuanceRepository.save(newCouponIssuance).getId();
	}
}
