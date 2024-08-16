package camp.woowak.lab.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.coupon.domain.CouponIssuance;

public interface CouponIssuanceRepository extends JpaRepository<CouponIssuance, Long> {
}
