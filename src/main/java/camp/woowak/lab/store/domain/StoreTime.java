package camp.woowak.lab.store.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreTime {

	@Column(nullable = false)
	private int startHour;
	@Column(nullable = false)
	private int startMinute;
	@Column(nullable = false)
	private int endHour;
	@Column(nullable = false)
	private int endMinute;

	public StoreTime(final LocalDateTime startTime, final LocalDateTime endTime) {
		this.startHour = startTime.getHour();
		this.startMinute = startTime.getMinute();

		this.endHour = endTime.getHour();
		this.endMinute = endTime.getMinute();
	}

	public LocalDateTime getStartTime() {
		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();
		int month = now.getMonthValue();
		int day = now.getDayOfMonth();
		return LocalDateTime.of(year, month, day, startHour, startMinute, 0, 0);
	}

	public LocalDateTime getEndTime() {
		LocalDateTime startTime = getStartTime();

		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();
		int month = now.getMonthValue();
		int day = now.getDayOfMonth();
		LocalDateTime endTime = LocalDateTime.of(year, month, day, endHour, endMinute, 0, 0);
		if (endTime.isBefore(startTime)) {
			endTime = endTime.plusDays(1);
		}

		return endTime;
	}
}
