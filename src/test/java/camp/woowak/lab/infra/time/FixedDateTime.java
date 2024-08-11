package camp.woowak.lab.infra.time;

import java.time.LocalDateTime;

import camp.woowak.lab.infra.date.DateTimeProvider;

/**
 * <h3> Sample Code </h3>
 * <h4> with Lambda </h4>
 * <pre>
 * {@code
 * DateTimeProvider fixedTimeProvider = () -> LocalDateTime.of(2024, 8, 24, 1, 0, 0);
 * LocalDateTime fixedTime = fixedTimeProvider.now();
 * }
 * </pre>
 *
 * <h4> with Constructor </h4>
 * <pre>
 * {@code
 * DateTimeProvider fixedTimeProvider = new FixedDateTime(2024, 8, 24, 1, 0, 0);
 * LocalDateTime fixedTime = fixedTimeProvider.now();
 * }
 * </pre>
 */
public class FixedDateTime implements DateTimeProvider {

	private final LocalDateTime fixedDateTime;

	public FixedDateTime(int year, int month, int day, int hour, int minute, int second) {
		fixedDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
	}

	@Override
	public LocalDateTime now() {
		return fixedDateTime;
	}

}
