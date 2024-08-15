package camp.woowak.lab.payaccount.service.command;

import java.util.UUID;

public record PayAccountChargeCommand(
	UUID customerId,
	long amount) {
}