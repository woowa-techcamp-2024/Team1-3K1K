package camp.woowak.lab.menu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
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

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT m FROM Menu m WHERE m.id = :id")
	Optional<Menu> findByIdForUpdate(Long id);

	/**
	 *
	 * 메뉴의 재고를 변경합니다.
	 * TODO: [논의] @Transactional을 Respository 단에 안둬도되는가?
	 * Repository 에서 직접 접근할 때 사용자가 실수해서 @Transactional 을 빼먹을 수도 있다.
	 */
	@Modifying
	@Query("UPDATE Menu m SET m.stockCount = :stock WHERE m.id = :id")
	int updateStock(@Param("id") Long id, @Param("stock") int stock);
}
