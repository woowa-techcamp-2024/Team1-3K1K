package camp.woowak.lab.web.dto.response.vendor;

import java.util.List;

import camp.woowak.lab.vendor.service.dto.VendorDTO;

public record RetrieveVendorResponse(List<VendorDTO> vendors) {
}
