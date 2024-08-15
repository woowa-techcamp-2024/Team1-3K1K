package camp.woowak.lab.menu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import camp.woowak.lab.menu.domain.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
	@Query("SELECT m FROM Menu m JOIN FETCH m.store WHERE m.id = :id")
	Optional<Menu> findByIdWithStore(@Param("id") Long id);
}
