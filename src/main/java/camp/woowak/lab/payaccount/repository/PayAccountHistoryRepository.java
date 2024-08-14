package camp.woowak.lab.payaccount.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.payaccount.domain.PayAccountHistory;

public interface PayAccountHistoryRepository extends JpaRepository<PayAccountHistory, Long> {
}
