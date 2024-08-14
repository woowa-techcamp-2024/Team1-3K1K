package camp.woowak.lab.vendor.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.exception.NotFoundVendorException;

public interface VendorRepository extends JpaRepository<Vendor, UUID> {
	Optional<Vendor> findByEmail(String email);

	default Vendor findByEmailOrThrow(String email) {
		return findByEmail(email).orElseThrow(NotFoundVendorException::new);
	}
}
