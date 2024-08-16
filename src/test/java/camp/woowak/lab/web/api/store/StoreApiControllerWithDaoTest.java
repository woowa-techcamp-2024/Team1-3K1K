package camp.woowak.lab.web.api.store;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.dao.store.StoreDummiesFixture;
import camp.woowak.lab.web.dto.request.store.StoreInfoListRequest;
import camp.woowak.lab.web.dto.request.store.StoreInfoListRequestConst;
import camp.woowak.lab.web.dto.response.store.StoreInfoListResponse;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("StoreApiController 클래스 With Dao")
@Transactional
public class StoreApiControllerWithDaoTest extends StoreDummiesFixture {
	private final ObjectMapper objectMapper;
	private final MockMvc mvc;
	private static List<Store> dummies;
	private final int dummyCount;
	private final int size = StoreInfoListRequest.DEFAULT_PAGE_SIZE;

	@Autowired
	public StoreApiControllerWithDaoTest(PayAccountRepository payAccountRepository, StoreRepository storeRepository,
										 StoreCategoryRepository storeCategoryRepository,
										 VendorRepository vendorRepository,
										 ObjectMapper objectMapper, MockMvc mvc) {
		super(storeRepository, storeCategoryRepository, vendorRepository, payAccountRepository);
		this.objectMapper = objectMapper;
		this.mvc = mvc;
		this.dummyCount = 105;
	}

	@BeforeEach
	public void setUpDummies() {
		dummies = createDummyStores(dummyCount);
	}

	@Nested
	@DisplayName("getStoreInfos메서드: GET /stores")
	class GetStoresInfo {
		private final String BASE_URL = "/stores";

		@Test
		@DisplayName("Default 사이즈 만큼 요청한 페이지의 컨텐츠가 나온다. [컨텐츠가 가득 찬 경우]")
		void testWithPageRequestOrderByIdAsc() throws Exception {
			//given
			int page = 0;

			//when & then
			ResultActions actions = mvc.perform(get(getBaseURL(page)))
				.andExpect(status().isOk());

			//기본적으로는 ID 오름차순
			int offset = page * size;
			assertResults(actions, dummies, (o1, o2) -> Long.compare(o1.getId(), o2.getId()), offset, size);
		}

		@Test
		@DisplayName("Default 사이즈 만큼 요청한 페이지의 컨텐츠가 나온다. [페이지가 모두 차지 않은 경우]")
		void testWithPageRequestOrderByIdAsc_Not_Enough_Stores() throws Exception {
			//given
			int page = 5;

			//when & then
			ResultActions actions = mvc.perform(get(getBaseURL(page)))
				.andExpect(status().isOk());

			//기본적으로는 ID 오름차순
			int offset = page * size;
			assertResults(actions, dummies, (o1, o2) -> Long.compare(o1.getId(), o2.getId()), offset, size);
		}

		private String getBaseURL(int page) {
			return BASE_URL + "?" + StoreInfoListRequestConst.PAGE_KEY + "=" + page;
		}
	}

	private void assertResults(ResultActions actions, List<Store> stores, Comparator<Store> comparator,
							   int offset, int size) throws
		UnsupportedEncodingException, JsonProcessingException {
		String responseString = actions.andReturn()
			.getResponse().getContentAsString();
		Map<String, Object> responseData = objectMapper.readValue(responseString, Map.class);
		String actualResponseDataString = objectMapper.writeValueAsString(responseData.get("data"));

		List<Store> expectedList = stores.stream()
			.sorted(comparator)
			.skip(offset)
			.limit(size)
			.toList();
		StoreInfoListResponse expectedResponse = StoreInfoListResponse.of(expectedList);
		String expectedResponseString = objectMapper.writeValueAsString(expectedResponse);

		assertThat(actualResponseDataString).isEqualTo(expectedResponseString);
	}
}
