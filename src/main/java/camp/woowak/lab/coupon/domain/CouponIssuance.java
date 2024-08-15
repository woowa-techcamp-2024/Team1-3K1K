package camp.woowak.lab.coupon.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import camp.woowak.lab.customer.domain.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class CouponIssuance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "coupon_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Coupon coupon;

	@JoinColumn(name = "customer_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Customer customer;

	@Column(nullable = false)
	@CreatedDate
	private LocalDateTime issuedAt;

	@Column
	private LocalDateTime usedAt;

	protected CouponIssuance() {
	}

	public CouponIssuance(Coupon coupon, Customer customer) {

		this.coupon = coupon;
		this.customer = customer;
		this.issuedAt = LocalDateTime.now();
	}
}
