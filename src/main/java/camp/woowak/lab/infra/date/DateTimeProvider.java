package camp.woowak.lab.infra.date;

import java.time.LocalDateTime;

/**
 * <h3> 인터페이스 도입 배경 </h3>
 * 메서드 내부 구현에 LocalDateTime.now 직접 사용하는 등 제어할 수 없는 영역을 인터페이스로 분리하여 제어 가능하도록 함 <br>
 * 날짜로 인해 테스트 어려운 코드를 테스트 용이하도록 하기 위함
 *
 * <h4> Production Level </h4>
 * 구현체: CurrentDateTime
 *
 * <h4> Test Level</h4>
 * 구현체: FixedDateTime
 */
public interface DateTimeProvider {

	LocalDateTime now();

}