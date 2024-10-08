package camp.woowak.lab.coupon.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import camp.woowak.lab.coupon.exception.DuplicateCouponTitleException;
import camp.woowak.lab.coupon.exception.InvalidCreationCouponException;
import camp.woowak.lab.coupon.repository.CouponRepository;
import camp.woowak.lab.coupon.service.command.CreateCouponCommand;

@SpringBootTest
class CreateCouponServiceIntegrationTest {
	@Autowired
	private CreateCouponService service;

	@Autowired
	private CouponRepository couponRepository;

	@Test
	@DisplayName("쿠폰 생성 테스트 - 중복된 제목 입력 시 실패 테스트")
	void testFailWhenDuplicatedTitle() {
		// given
		String title = "테스트 쿠폰";
		int discountAmount = 1000;
		int quantity = 100;
		LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);
		CreateCouponCommand cmd = new CreateCouponCommand(title, discountAmount, quantity, expiredAt);

		service.createCoupon(cmd);

		// when & then
		assertThrows(DuplicateCouponTitleException.class,
			() -> service.createCoupon(cmd));
	}

	@Test
	@DisplayName("쿠폰 생성 테스트 - 잘못된 제목 입력 시 하위 예외 전파 테스트")
	void testExceptionPropagationWhenInvalidTitle() {
		// given
		String title = "";
		int discountAmount = 1000;
		int quantity = 100;
		LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);
		CreateCouponCommand cmd = new CreateCouponCommand(title, discountAmount, quantity, expiredAt);

		// when & then
		assertThrows(InvalidCreationCouponException.class,
			() -> service.createCoupon(cmd));
	}

	@Test
	@DisplayName("쿠폰 생성 테스트 - 잘못된 수량 입력 시 하위 예외 전파 테스트")
	void testExceptionPropagationWhenInvalidQuantity() {
		// given
		String title = "테스트 쿠폰";
		int discountAmount = 1000;
		int quantity = -1;
		LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);
		CreateCouponCommand cmd = new CreateCouponCommand(title, discountAmount, quantity, expiredAt);

		// when & then
		assertThrows(InvalidCreationCouponException.class,
			() -> service.createCoupon(cmd));
	}

	@Test
	@DisplayName("쿠폰 생성 테스트 - 잘못된 할인 금액 입력 시 하위 예외 전파 테스트")
	void testExceptionPropagationWhenInvalidDiscountAmount() {
		// given
		String title = "테스트 쿠폰";
		int discountAmount = -1;
		int quantity = 100;
		LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);
		CreateCouponCommand cmd = new CreateCouponCommand(title, discountAmount, quantity, expiredAt);

		// when & then
		assertThrows(InvalidCreationCouponException.class,
			() -> service.createCoupon(cmd));
	}

	@Test
	@DisplayName("쿠폰 생성 테스트 - 잘못된 만료일 입력 시 하위 예외 전파 테스트")
	void testExceptionPropagationWhenInvalidExpiredAt() {
		// given
		String title = "테스트 쿠폰";
		int discountAmount = 1000;
		int quantity = 100;
		LocalDateTime expiredAt = LocalDateTime.now().minusDays(7);
		CreateCouponCommand cmd = new CreateCouponCommand(title, discountAmount, quantity, expiredAt);

		// when & then
		assertThrows(InvalidCreationCouponException.class,
			() -> service.createCoupon(cmd));
	}
}
