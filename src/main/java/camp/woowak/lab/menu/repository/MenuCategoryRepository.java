package camp.woowak.lab.menu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.menu.domain.MenuCategory;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

	Optional<MenuCategory> findByStoreIdAndName(Long storeId, String name);

}
