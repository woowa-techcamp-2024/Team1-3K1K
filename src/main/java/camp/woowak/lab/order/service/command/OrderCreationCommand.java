package camp.woowak.lab.order.service.command;

import java.util.UUID;

public record OrderCreationCommand(
	UUID requesterId
) {
}
