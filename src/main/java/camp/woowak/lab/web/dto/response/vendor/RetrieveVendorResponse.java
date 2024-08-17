package camp.woowak.lab.web.dto.response.vendor;

import java.util.List;

import camp.woowak.lab.vendor.domain.Vendor;

public record RetrieveVendorResponse(List<Vendor> vendors) {
}
