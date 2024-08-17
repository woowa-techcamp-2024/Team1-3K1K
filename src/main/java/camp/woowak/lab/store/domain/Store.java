package camp.woowak.lab.store.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import camp.woowak.lab.store.exception.NotEqualsOwnerException;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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
		StoreValidator.validate(owner, storeCategory, name, address, minOrderPrice, startTime, endTime);
		this.owner = owner;
		this.storeCategory = storeCategory;
		this.name = name;
		this.storeAddress = new StoreAddress(address);
		this.phoneNumber = phoneNumber;
		this.minOrderPrice = minOrderPrice;
		this.storeTime = new StoreTime(startTime, endTime);
	}

	public void validateOwner(Vendor owner) {
		if (!this.owner.equals(owner)) {
			throw new NotEqualsOwnerException("가게의 점주가 일치하지 않습니다." + this.owner + ", " + owner);
		}
	}

	public boolean isOwnedBy(UUID ownerId) {
		return owner.getId().equals(ownerId);
	}

	public boolean isOpen() {
		LocalDateTime openTime = storeTime.getStartTime();
		LocalDateTime closeTime = storeTime.getEndTime();

		LocalDateTime now = LocalDateTime.now();

		return (now.isEqual(openTime) || now.isAfter(openTime)) && now.isBefore(closeTime);
	}

	public String getStoreAddress() {
		return storeAddress.getDistrict();
	}

	public Long getStoreCategoryId() {
		return storeCategory.getId();
	}

	public String getStoreCategoryName() {
		return storeCategory.getName();
	}

	public LocalTime getStoreStartTime() {
		return storeTime.getStartTime().toLocalTime();
	}

	public LocalTime getStoreEndTime() {
		return storeTime.getEndTime().toLocalTime();
	}

	public UUID getVendorId() {
		return owner.getId();
	}

	public String getVendorName() {
		return owner.getName();
	}

}
