package camp.woowak.lab.payaccount.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.domain.PayAccountHistory;
import camp.woowak.lab.payaccount.exception.NotFoundAccountException;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.payaccount.service.command.PayAccountChargeCommand;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PayAccountChargeService {
	private final PayAccountRepository payAccountRepository;

	public PayAccountChargeService(PayAccountRepository payAccountRepository) {
		this.payAccountRepository = payAccountRepository;
	}

	@Transactional
	public long chargeAccount(PayAccountChargeCommand command) {
		PayAccount payAccount = payAccountRepository.findByCustomerIdForUpdate(command.customerId())
			.orElseThrow(() -> new NotFoundAccountException("Invalid account id with " + command.customerId()));

		PayAccountHistory chargeHistory = payAccount.charge(command.amount());
		log.info("A Charge of {} has been completed from Account ID {}", command.amount(), payAccount.getId());

		return payAccount.getBalance();
	}
}
