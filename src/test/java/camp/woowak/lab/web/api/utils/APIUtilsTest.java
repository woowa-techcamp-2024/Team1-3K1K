package camp.woowak.lab.web.api.utils;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;

@DisplayName("APIUtils 클래스")
class APIUtilsTest {
	@Nested
	@DisplayName("of메서드의")
	class OfTest {
		@Nested
		@DisplayName("HttpStatus 값과 Data를 파라미터로 받는 메서드는")
		class ParamWithHttpStatusAndData {
			@Test
			@DisplayName("data와 status를 가지는 APIResponse를 생성할 수 있다.")
			void APIResponseWithHttpStatusAndData() throws JsonProcessingException {
				//given
				HttpStatus status = HttpStatus.OK;
				String message = "hello world";

				//when
				ResponseEntity<APIResponse<String>> apiResponse = APIUtils.of(status, message);

				//then
				assertThat(apiResponse.getStatusCode()).isEqualTo(status);
				assertThat(apiResponse.getBody().getData()).isEqualTo(message);
				assertThat(apiResponse.getBody().getStatus()).isEqualTo(status.value());
			}

			@Test
			@DisplayName("data가 객체인 경우도 APIResponse를 생성할 수 있다.")
			void APIResponseWithObjectData() throws JsonProcessingException {
				//given
				HttpStatus status = HttpStatus.OK;
				Example example = new Example(27, "Hyeon-Uk");

				//when
				ResponseEntity<APIResponse<Example>> apiResponse = APIUtils.of(status, example);

				//then
				assertThat(apiResponse.getStatusCode()).isEqualTo(status);
				assertThat(apiResponse.getBody().getData()).isEqualTo(example);
				assertThat(apiResponse.getBody().getStatus()).isEqualTo(status.value());
			}

			private class Example {
				int age;
				String name;

				public Example(int age, String name) {
					this.age = age;
					this.name = name;
				}

				public int getAge() {
					return age;
				}

				public String getName() {
					return name;
				}
			}
		}

	}
}