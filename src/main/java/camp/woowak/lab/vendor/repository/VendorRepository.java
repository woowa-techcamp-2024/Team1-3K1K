package camp.woowak.lab.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.vendor.domain.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
}
