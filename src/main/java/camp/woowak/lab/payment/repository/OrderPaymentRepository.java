package camp.woowak.lab.payment.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import camp.woowak.lab.payment.domain.OrderPayment;
import camp.woowak.lab.payment.domain.OrderPaymentStatus;

public interface OrderPaymentRepository extends JpaRepository<OrderPayment, Long> {

	@Query("SELECT op FROM OrderPayment op "
		+ "WHERE op.recipient.id = :recipientId "
		+ "AND op.orderPaymentStatus = :orderPaymentStatus")
	List<OrderPayment> findByRecipientIdAndOrderPaymentStatus(@Param("recipientId") UUID recipientId,
															  @Param("orderPaymentStatus") OrderPaymentStatus orderPaymentStatus);

}
