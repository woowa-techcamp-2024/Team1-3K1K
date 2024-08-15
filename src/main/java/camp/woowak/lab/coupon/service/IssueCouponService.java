package camp.woowak.lab.coupon.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import camp.woowak.lab.coupon.domain.Coupon;
import camp.woowak.lab.coupon.exception.DuplicateCouponTitleException;
import camp.woowak.lab.coupon.repository.CouponRepository;
import camp.woowak.lab.coupon.service.command.IssueCouponCommand;
import jakarta.transaction.Transactional;

@Service
public class IssueCouponService {
	private final CouponRepository couponRepository;

	public IssueCouponService(CouponRepository couponRepository) {
		this.couponRepository = couponRepository;
	}

	/**
	 *
	 * @throw InvalidCreationCouponException 쿠폰 생성 시 유효하지 않은 값이 입력되었을 경우
	 * @throw DuplicateCouponTitleException 쿠폰 생성 시 중복된 제목이 입력되었을 경우
	 */
	@Transactional
	public Long issueCoupon(IssueCouponCommand cmd) {
		Coupon newCoupon = new Coupon(cmd.title(), cmd.discountAmount(), cmd.quantity(), cmd.expiredAt());
		try {
			return couponRepository.save(newCoupon).getId();
		} catch (DataIntegrityViolationException e) {
			throw new DuplicateCouponTitleException(cmd.title() + "은 이미 존재하는 쿠폰 제목입니다.");
		}
	}
}
