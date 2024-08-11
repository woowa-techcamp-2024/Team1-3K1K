package camp.woowak.lab.payaccount.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.domain.PayAccountHistory;
import camp.woowak.lab.payaccount.exception.NotFoundAccountException;
import camp.woowak.lab.payaccount.repository.PayAccountHistoryRepository;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.payaccount.service.command.AccountTransactionCommand;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PayAccountDepositService {
	private final PayAccountRepository payAccountRepository;
	private final PayAccountHistoryRepository payAccountHistoryRepository;

	public PayAccountDepositService(PayAccountRepository payAccountRepository,
									PayAccountHistoryRepository payAccountHistoryRepository) {
		this.payAccountRepository = payAccountRepository;
		this.payAccountHistoryRepository = payAccountHistoryRepository;
	}

	@Transactional
	public long depositAccount(AccountTransactionCommand command) {
		PayAccount payAccount = payAccountRepository.findByIdForUpdate(command.payAccountId())
			.orElseThrow(() -> new NotFoundAccountException("Invalid account id with " + command.payAccountId()));

		PayAccountHistory depositHistory = payAccount.deposit(command.amount());
		log.info("A deposit of {} has been completed into Account ID {}.", command.amount(), command.payAccountId());
		payAccountHistoryRepository.save(depositHistory);

		return payAccount.getBalance();
	}
}
