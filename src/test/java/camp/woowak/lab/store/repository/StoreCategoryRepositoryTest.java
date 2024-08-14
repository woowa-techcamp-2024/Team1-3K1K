package camp.woowak.lab.store.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import camp.woowak.lab.store.domain.StoreCategory;

@DataJpaTest
class StoreCategoryRepositoryTest {

	@Autowired
	StoreCategoryRepository storeCategoryRepository;

	@Nested
	@DisplayName("가게 카테고리를 이름으로 조회하는 기능은")
	class FindByNameTest {

		@Test
		@DisplayName("[Success] 존재하는 가게 카테고리로 조회하면 Optional 에 가게 카테고리가 있다")
		void existStoreCategory() {
			// given
			String storeCategoryName = "양식";
			StoreCategory storeCategory = new StoreCategory(storeCategoryName);
			storeCategoryRepository.saveAndFlush(storeCategory);

			// when
			Optional<StoreCategory> findResult = storeCategoryRepository.findByName(storeCategoryName);

			// then
			assertThat(findResult)
				.isPresent()
				.containsSame(storeCategory);
		}

		@Test
		@DisplayName("[Exception] 없는 가게 카테고리로 조회하면 빈 값을 반환한다")
		void notExistStoreCategory() {
			// given
			String notExistStoreCategoryName = "없는가게카테고리";

			// when & then
			assertThat(storeCategoryRepository.findByName(notExistStoreCategoryName))
				.isEmpty();
		}

	}

}