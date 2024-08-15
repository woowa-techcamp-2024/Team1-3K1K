package camp.woowak.lab.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.store.domain.StoreCategory;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {

	Optional<StoreCategory> findByName(String name);

}
