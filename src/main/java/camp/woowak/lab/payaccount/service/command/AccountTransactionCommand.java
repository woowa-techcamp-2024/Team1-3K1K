package camp.woowak.lab.payaccount.service.command;

public record AccountTransactionCommand(
	Long payAccountId,
	long amount) {
}