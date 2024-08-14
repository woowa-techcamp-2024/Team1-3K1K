package camp.woowak.lab.vendor;

import java.util.UUID;

import camp.woowak.lab.vendor.domain.Vendor;

public class TestVendor extends Vendor {
	private UUID id;
	private Vendor vendor;

	public TestVendor(UUID id, Vendor vendor) {
		this.id = id;
		this.vendor = vendor;
	}

	public UUID getId() {
		return id;
	}
}
