package camp.woowak.lab.vendor.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.vendor.domain.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, UUID> {
}
