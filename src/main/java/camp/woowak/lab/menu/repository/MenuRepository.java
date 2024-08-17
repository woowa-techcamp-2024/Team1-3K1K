package camp.woowak.lab.menu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import camp.woowak.lab.menu.domain.Menu;
import jakarta.persistence.LockModeType;

public interface MenuRepository extends JpaRepository<Menu, Long> {
	@Query("SELECT m FROM Menu m JOIN FETCH m.store WHERE m.id = :id")
	Optional<Menu> findByIdWithStore(@Param("id") Long id);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT m FROM Menu m where m.id in :ids")
	List<Menu> findAllByIdForUpdate(List<Long> ids);
}
