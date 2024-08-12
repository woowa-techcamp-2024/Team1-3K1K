package camp.woowak.lab.vendor.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.domain.TestPayAccount;
import camp.woowak.lab.vendor.exception.InvalidVendorCreationException;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.authentication.PasswordEncoder;

class VendorTest {

	private PayAccount payAccount;
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		payAccount = new TestPayAccount(1L);
		passwordEncoder = new NoOpPasswordEncoder();
	}

	@Nested
	@DisplayName("Vendor 생성은")
	class IsConstructed {
		@Nested
		@DisplayName("이름이")
		class NameMust {
			@Test
			@DisplayName("[성공] 50자까지 허용한다.")
			void successWith50() {
				Assertions.assertDoesNotThrow(
					() -> new Vendor("aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeee", "validEmail@validEmail.com",
						"validPassword", "010-0000-0000", payAccount, passwordEncoder));
			}

			@Test
			@DisplayName("[예외] null이면 예외가 발생한다.")
			void failWithNull() {
				Assertions.assertThrows(InvalidVendorCreationException.class,
					() -> new Vendor(null, "validEmail@validEmail.com", "validPassword", "010-0000-0000", payAccount,
						passwordEncoder));
			}

			@Test
			@DisplayName("[예외] 공란이면 예외가 발생한다.")
			void failWithBlank() {
				Assertions.assertThrows(InvalidVendorCreationException.class,
					() -> new Vendor(" ", "validEmail@validEmail.com", "validPassword", "010-0000-0000", payAccount,
						passwordEncoder));
			}

			@Test
			@DisplayName("[예외] 50자를 초과하면 예외가 발생한다.")
			void failWith51() {
				Assertions.assertThrows(InvalidVendorCreationException.class,
					() -> new Vendor("aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeeef", "validEmail@validEmail.com",
						"validPassword", "010-0000-0000", payAccount, passwordEncoder));
			}
		}

		@Nested
		@DisplayName("이메일이")
		class EmailMust {
			@Test
			@DisplayName("[성공] 100자까지 허용한다.")
			void successWith100() {
				Assertions.assertDoesNotThrow(
					() -> new Vendor("aaaaaaaaaa",
						"aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeeeaaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeee",
						"validPassword", "010-0000-0000", payAccount, passwordEncoder));
			}

			@Test
			@DisplayName("[예외] null이면 예외가 발생한다.")
			void failWithNull() {
				Assertions.assertThrows(InvalidVendorCreationException.class,
					() -> new Vendor("aaaaaaaaaa", null, "validPassword", "010-0000-0000", payAccount,
						passwordEncoder));
			}

			@Test
			@DisplayName("[예외] 공란이면 예외가 발생한다.")
			void failWithBlank() {
				Assertions.assertThrows(InvalidVendorCreationException.class,
					() -> new Vendor("aaaaaaaaaa", " ", "validPassword", "010-0000-0000", payAccount, passwordEncoder));
			}

			@Test
			@DisplayName("[예외] 100자를 초과하면 예외가 발생한다.")
			void failWith101() {
				Assertions.assertThrows(InvalidVendorCreationException.class,
					() -> new Vendor("aaaaaaaaaa",
						"aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeeeaaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeeea",
						"validPassword", "010-0000-0000", payAccount, passwordEncoder));
			}
		}

		@Nested
		@DisplayName("비밀번호가")
		class PasswordMust {
			@Test
			@DisplayName("[성공] 8자 이상부터 허용한다.")
			void successWith8() {
				Assertions.assertDoesNotThrow(
					() -> new Vendor("aaaaaaaaaa", "validEmail@validEmail.com", "thisis8c",
						"010-0000-0000", payAccount, passwordEncoder));
			}

			@Test
			@DisplayName("[성공] 30자까지 허용한다.")
			void successWith30() {
				Assertions.assertDoesNotThrow(
					() -> new Vendor("aaaaaaaaaa", "validEmail@validEmail.com", "thisstringsizeisthirtyalsnvien",
						"010-0000-0000", payAccount, passwordEncoder));
			}

			@Test
			@DisplayName("[예외] null이면 예외가 발생한다.")
			void failWithNull() {
				Assertions.assertThrows(InvalidVendorCreationException.class,
					() -> new Vendor("aaaaaaaaaa", "validEmail@validEmail.com", null, "010-0000-0000", payAccount,
						passwordEncoder));
			}

			@Test
			@DisplayName("[예외] 공란이면 예외가 발생한다.")
			void failWithBlank() {
				Assertions.assertThrows(InvalidVendorCreationException.class,
					() -> new Vendor("aaaaaaaaaa", "validEmail@validEmail.com", " ", "010-0000-0000", payAccount,
						passwordEncoder));
			}

			@Test
			@DisplayName("[예외] 8자 미만이면 예외가 발생한다.")
			void failWith7() {
				Assertions.assertThrows(InvalidVendorCreationException.class,
					() -> new Vendor("aaaaaaaaaa", "validEmail@validEmail.com", "thisis7",
						"010-0000-0000", payAccount, passwordEncoder));
			}

			@Test
			@DisplayName("[예외] 30자를 초과하면 예외가 발생한다.")
			void failWith31() {
				Assertions.assertThrows(InvalidVendorCreationException.class,
					() -> new Vendor("aaaaaaaaaa", "validEmail@validEmail.com", "thisstringsizeisthirtyonesnvien",
						"010-0000-0000", payAccount, passwordEncoder));
			}
		}

		@Nested
		@DisplayName("전화번호가")
		class PhoneMust {
			@Test
			@DisplayName("[성공] 30자까지 허용한다.")
			void successWith30() {
				Assertions.assertDoesNotThrow(
					() -> new Vendor("aaaaaaaaaa", "validEmail@validEmail.com", "validPassword",
						"0000000000-0000000000-00000000",
						payAccount, passwordEncoder));
			}

			@Test
			@DisplayName("[예외] null이면 예외가 발생한다.")
			void failWithNull() {
				Assertions.assertThrows(InvalidVendorCreationException.class,
					() -> new Vendor("aaaaaaaaaa", "validEmail@validEmail.com", "validPassword", null,
						payAccount, passwordEncoder));
			}

			@Test
			@DisplayName("[예외] 공란이면 예외가 발생한다.")
			void failWithBlank() {
				Assertions.assertThrows(InvalidVendorCreationException.class,
					() -> new Vendor("aaaaaaaaaa", "validEmail@validEmail.com", "validPassword", " ",
						payAccount, passwordEncoder));
			}

			@Test
			@DisplayName("[예외] 30자를 초과하면 예외가 발생한다.")
			void failWith31() {
				Assertions.assertThrows(InvalidVendorCreationException.class,
					() -> new Vendor("aaaaaaaaaa", "validEmail@validEmail.com", "validPassword",
						"0000000000-0000000000-000000000",
						payAccount, passwordEncoder));
			}
		}

		@Nested
		@DisplayName("페이계좌가")
		class PayAccountMust {
			@Test
			@DisplayName("[성공] 있으면 성공한다.")
			void successWithExist() {
				Assertions.assertDoesNotThrow(
					() -> new Vendor("validName", "validEmail@validEmail.com", "validPassword", "010-0000-0000",
						payAccount, passwordEncoder));
			}

			@Test
			@DisplayName("[예외] null이면 예외가 발생한다.")
			void failWithNull() {
				Assertions.assertThrows(InvalidVendorCreationException.class,
					() -> new Vendor("aaaaaaaaaa", "validEmail@validEmail.com", "validPassword", "010-0000-0000", null,
						passwordEncoder));
			}
		}
	}
}
