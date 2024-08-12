package camp.woowak.lab.payaccount.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.domain.PayAccountHistory;
import camp.woowak.lab.payaccount.exception.NotFoundAccountException;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.payaccount.service.command.AccountTransactionCommand;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PayAccountDepositService {
	private final PayAccountRepository payAccountRepository;

	public PayAccountDepositService(PayAccountRepository payAccountRepository) {
		this.payAccountRepository = payAccountRepository;
	}

	@Transactional
	public long depositAccount(AccountTransactionCommand command) {
		PayAccount payAccount = payAccountRepository.findByIdForUpdate(command.payAccountId())
			.orElseThrow(() -> new NotFoundAccountException("Invalid account id with " + command.payAccountId()));

		PayAccountHistory depositHistory = payAccount.deposit(command.amount());
		log.info("A deposit of {} has been completed into Account ID {}.", command.amount(), command.payAccountId());

		return payAccount.getBalance();
	}
}
