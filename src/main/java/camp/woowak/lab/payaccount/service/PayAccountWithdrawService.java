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
public class PayAccountWithdrawService {
	private final PayAccountRepository payAccountRepository;
	private final PayAccountHistoryRepository payAccountHistoryRepository;

	public PayAccountWithdrawService(PayAccountRepository payAccountRepository,
									 PayAccountHistoryRepository payAccountHistoryRepository) {
		this.payAccountRepository = payAccountRepository;
		this.payAccountHistoryRepository = payAccountHistoryRepository;
	}

	@Transactional
	public long withdrawAccount(AccountTransactionCommand command) {
		PayAccount payAccount = payAccountRepository.findByIdForUpdate(command.payAccountId())
			.orElseThrow(() -> new NotFoundAccountException("Invalid account id with " + command.payAccountId()));

		PayAccountHistory withdrawHistory = payAccount.withdraw(command.amount());
		log.info("A withdrawal of {} has been completed from Account ID {}", command.amount(), command.payAccountId());
		payAccountHistoryRepository.save(withdrawHistory);

		return payAccount.getBalance();
	}
}
