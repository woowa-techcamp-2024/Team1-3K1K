package camp.woowak.lab.payaccount.service.command;

public record PayAccountChargeCommand(
	Long customerId,
	long amount) {
}