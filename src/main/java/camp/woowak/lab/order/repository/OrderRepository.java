package camp.woowak.lab.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.order.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
