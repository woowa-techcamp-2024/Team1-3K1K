package camp.woowak.lab.vendor.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;

@Service
@Transactional(readOnly = true)
public class RetrieveVendorService {
	private final VendorRepository vendorRepository;

	public RetrieveVendorService(VendorRepository vendorRepository) {
		this.vendorRepository = vendorRepository;
	}

	public List<Vendor> retrieveVendors() {
		return vendorRepository.findAll();
	}
}
