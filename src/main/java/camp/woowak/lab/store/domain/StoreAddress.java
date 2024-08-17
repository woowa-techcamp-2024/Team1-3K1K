package camp.woowak.lab.store.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreAddress {

	public static final String DEFAULT_DISTRICT = "송파";

	@Column(nullable = false)
	private String district;

	public StoreAddress(final String district) {
		this.district = district;
	}

	public String getDistrict() {
		return district;
	}
}
