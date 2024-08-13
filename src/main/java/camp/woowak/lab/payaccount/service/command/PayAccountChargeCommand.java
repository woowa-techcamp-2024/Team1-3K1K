package camp.woowak.lab.payaccount.service.command;

public record PayAccountChargeCommand(
	Long payAccountId,
	long amount) {
}