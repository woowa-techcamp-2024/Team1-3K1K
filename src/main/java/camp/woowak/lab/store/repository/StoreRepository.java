package camp.woowak.lab.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.store.domain.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {

	Optional<Store> findByOwnerId(Long ownerId);

}
