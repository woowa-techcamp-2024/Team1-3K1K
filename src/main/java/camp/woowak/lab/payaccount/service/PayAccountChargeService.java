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

	/**
	 * @throws camp.woowak.lab.payaccount.exception.NotFoundAccountException    계좌를 찾지 못함. 존재하지 않는 계좌
	 * @throws camp.woowak.lab.payaccount.exception.DailyLimitExceededException 일일 충전 한도를 초과함
	 */
	@Transactional
	public long chargeAccount(PayAccountChargeCommand command) {
		PayAccount payAccount = payAccountRepository.findByCustomerIdForUpdate(command.customerId())
			.orElseThrow(() -> {
				log.warn("Invalid account id with {}", command.customerId());
				throw new NotFoundAccountException();
			});

		PayAccountHistory chargeHistory = payAccount.charge(command.amount());
		log.info("A Charge of {} has been completed from Account ID {}", command.amount(), payAccount.getId());

		return payAccount.getBalance();
	}
}
