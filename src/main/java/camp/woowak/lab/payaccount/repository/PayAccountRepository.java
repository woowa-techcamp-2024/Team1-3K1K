package camp.woowak.lab.payaccount.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import camp.woowak.lab.payaccount.domain.PayAccount;

public interface PayAccountRepository extends JpaRepository<PayAccount, Long> {
}