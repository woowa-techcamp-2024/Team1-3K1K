package camp.woowak.lab.coupon.domain;

import static camp.woowak.lab.coupon.domain.CouponValidator.*;

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
@Table(name = "Coupons", uniqueConstraints = {
	@jakarta.persistence.UniqueConstraint(name = "UK_Coupons_title", columnNames = {"title"})
})
public class Coupon {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false)
	private int discountAmount;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime expiredAt;

	protected Coupon() {
	}

	public Coupon(String title, int discountAmount, int quantity, LocalDateTime expiredAt) {
		validate(title, discountAmount, quantity, expiredAt);
		this.title = title;
		this.discountAmount = discountAmount;
		this.quantity = quantity;
		this.expiredAt = expiredAt;
	}
}
