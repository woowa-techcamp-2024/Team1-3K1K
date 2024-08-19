package camp.woowak.lab.store.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
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
	@Transient
	private boolean overDay;

	public StoreTime(final LocalDateTime startTime, final LocalDateTime endTime) {
		this.startHour = startTime.getHour();
		this.startMinute = startTime.getMinute();

		this.endHour = endTime.getHour();
		this.endMinute = endTime.getMinute();

		this.overDay = startTime.toLocalDate().isBefore(endTime.toLocalDate());
	}

	public LocalDateTime getStartTime() {
		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();
		int month = now.getMonthValue();
		int day = now.getDayOfMonth();
		return LocalDateTime.of(year, month, day, startHour, startMinute, 0, 0);
	}

	public LocalDateTime getEndTime() {
		LocalDateTime now = LocalDateTime.now().plusDays(overDay ? 1 : 0);
		int year = now.getYear();
		int month = now.getMonthValue();
		int day = now.getDayOfMonth();
		return LocalDateTime.of(year, month, day, endHour, endMinute, 0, 0);
	}
}
