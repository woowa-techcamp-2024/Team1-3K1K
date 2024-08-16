package camp.woowak.lab.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.coupon.domain.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
