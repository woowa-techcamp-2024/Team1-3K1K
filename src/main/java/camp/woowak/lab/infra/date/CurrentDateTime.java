package camp.woowak.lab.infra.date;

import java.time.LocalDateTime;

public class CurrentDateTime implements DateTimeProvider {

	@Override
	public LocalDateTime now() {
		return LocalDateTime.now();
	}

}
