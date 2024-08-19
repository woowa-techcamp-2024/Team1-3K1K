package camp.woowak.lab.order.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import camp.woowak.lab.order.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	@Query("SELECT o FROM Order o JOIN FETCH o.store s WHERE s.owner.id = :vendorId")
	List<Order> findAllByOwner(UUID vendorId);

	@Query("SELECT o FROM Order o JOIN FETCH o.store s WHERE s.id = :storeId AND s.owner.id = :vendorId")
	List<Order> findByStore(Long storeId, UUID vendorId);

	Page<Order> findAllByStore_Owner_Id(UUID vendorId, Pageable pageable);

	Page<Order> findByStore_Id(Long storeId, Pageable pageable);
}
