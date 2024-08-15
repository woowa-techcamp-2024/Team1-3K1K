package camp.woowak.lab.coupon.domain;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "coupons")
public class Coupon {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private int discountAmount;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime expiredAt;

	protected Coupon() {
	}

	public Coupon(int discountAmount, int quantity, LocalDateTime expiredAt) {
		this.discountAmount = discountAmount;
		this.quantity = quantity;
		this.expiredAt = expiredAt;
	}
}
