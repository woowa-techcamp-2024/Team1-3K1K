package camp.woowak.lab.store.domain;

import java.time.LocalDateTime;

import camp.woowak.lab.vendor.domain.Vendor;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vendor_id", nullable = false)
	private Vendor owner;

	@OneToOne
	@JoinColumn(name = "store_category_id", nullable = false)
	private StoreCategory storeCategory;

	@Column(nullable = false)
	private String name;

	// TODO: 위치 정보에 대한 요구사항 논의 후 수정 예정.
	//  i.g) 송파구로 특정, 도시 정보로 특정 등 요구사항이 정의되어야 엔티티 설계를 진행할 수 있음
	@Embedded
	private StoreAddress storeAddress;

	@Column(nullable = false)
	private String phoneNumber;

	@Column(nullable = false)
	private Integer minOrderPrice;

	@Embedded
	private StoreTime storeTime;

	public Store(Vendor owner, StoreCategory storeCategory, String name, String address, String phoneNumber,
				 Integer minOrderPrice, LocalDateTime startTime, LocalDateTime endTime
	) {
		StoreValidator.validate(name, address, minOrderPrice, startTime, endTime);
		this.owner = owner;
		this.storeCategory = storeCategory;
		this.name = name;
		this.storeAddress = new StoreAddress(address);
		this.phoneNumber = phoneNumber;
		this.minOrderPrice = minOrderPrice;
		this.storeTime = new StoreTime(startTime, endTime);
	}

}
