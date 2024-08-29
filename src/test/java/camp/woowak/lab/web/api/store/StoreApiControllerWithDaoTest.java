package camp.woowak.lab.web.api.store;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.infra.cache.FakeMenuStockCacheService;
import camp.woowak.lab.menu.repository.MenuCategoryRepository;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.order.repository.OrderRepository;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.dao.store.StoreDummiesFixture;
import camp.woowak.lab.web.dto.request.store.StoreFilterBy;
import camp.woowak.lab.web.dto.request.store.StoreInfoListRequestConst;
import camp.woowak.lab.web.dto.request.store.StoreSortBy;
import camp.woowak.lab.web.dto.response.store.StoreInfoListResponse;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("StoreApiController 클래스 With Dao")
@Transactional
public class StoreApiControllerWithDaoTest extends StoreDummiesFixture {
	private final ObjectMapper objectMapper;
	private final MockMvc mvc;
	private final int size = StoreInfoListRequestConst.DEFAULT_PAGE_SIZE;
	private final String sortByKey = "sortBy";
	private final String filterByKey = "filterBy";
	private final String filterValueKey = "filterValue";
	private final String pageKey = "page";
	private final String orderKey = "order";

	private final MenuRepository menuRepository;

	@Autowired
	public StoreApiControllerWithDaoTest(PayAccountRepository payAccountRepository, StoreRepository storeRepository,
										 StoreCategoryRepository storeCategoryRepository,
										 VendorRepository vendorRepository,
										 OrderRepository orderRepository,
										 CustomerRepository customerRepository,
										 MenuRepository menuRepository,
										 MenuCategoryRepository menuCategoryRepository,
										 ObjectMapper objectMapper, MockMvc mvc) {
		super(storeRepository, storeCategoryRepository, vendorRepository, payAccountRepository, orderRepository,
			customerRepository, menuRepository, menuCategoryRepository, new FakeMenuStockCacheService(), new NoOpPasswordEncoder());
		this.menuRepository = menuRepository;
		this.objectMapper = objectMapper;
		this.mvc = mvc;
	}

	@Nested
	@DisplayName("getStoreInfos메서드: GET /stores")
	class GetStoresInfo {
		private final String BASE_URL = "/stores";
		private final int dummyCount = 105;
		private List<Store> dummies;

		@BeforeEach
		@Transactional
		void setUpDummies() {
			dummies = createDummyStores(dummyCount);
		}

		@Nested
		@DisplayName("페이징 처리를")
		class Paging {
			@Test
			@DisplayName("Default 사이즈 만큼 요청한 페이지의 컨텐츠가 나온다. [컨텐츠가 가득 찬 경우]")
			void testWithPageRequestOrderByIdAsc() throws Exception {
				//given
				int page = 0;

				//when & then
				ResultActions actions = mvc.perform(get(BASE_URL)
						.param(pageKey, Integer.toString(page)))
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
				ResultActions actions = mvc.perform(get(BASE_URL)
						.param(pageKey, Integer.toString(page)))
					.andExpect(status().isOk());

				//기본적으로는 ID 오름차순
				int offset = page * size;
				assertResults(actions, dummies, (o1, o2) -> Long.compare(o1.getId(), o2.getId()), offset, size);
			}
		}

		@Nested
		@DisplayName("sortBy 처리를")
		class SortBy {
			@Nested
			@DisplayName("MIN_PRICE는 (기본 order = 0)")
			class MinPrice {
				int page = 0;

				@Test
				@DisplayName("해당 페이지가 최소 주문 금액 오름차순 정렬되어 나온다.")
				void testWithPageRequestOrderByPriceAscWithDefaultOrder() throws Exception {
					//given
					StoreSortBy sortBy = StoreSortBy.MIN_PRICE;

					//when & then
					ResultActions actions = mvc.perform(get(BASE_URL)
							.param(pageKey, Integer.toString(page))
							.param(sortByKey, sortBy.value()))
						.andExpect(status().isOk());

					//then
					Comparator<Store> minPriceAsc = (o1, o2) -> Long.compare(o1.getMinOrderPrice(),
						o2.getMinOrderPrice());
					assertResults(actions, dummies, minPriceAsc, page, size);
				}

				@Test
				@DisplayName("order = 0을 넣으면 해당 페이지가 최소 주문 금액 오름차순 정렬되어 나온다.")
				void testWithPageRequestOrderByPriceAscWithOrderValue() throws Exception {
					//given
					StoreSortBy sortBy = StoreSortBy.MIN_PRICE;
					int order = 0;

					//when & then
					ResultActions actions = mvc.perform(get(BASE_URL)
							.param(pageKey, Integer.toString(page))
							.param(sortByKey, sortBy.value())
							.param(orderKey, Integer.toString(order)))
						.andExpect(status().isOk());

					//then
					Comparator<Store> minPriceAsc = (o1, o2) -> Long.compare(o1.getMinOrderPrice(),
						o2.getMinOrderPrice());
					assertResults(actions, dummies, minPriceAsc, page, size);
				}

				@Test
				@DisplayName("order = 1을 넣으면 해당 페이지가 최소 주문 금액 내림차순 정렬되어 나온다.")
				void testWithPageRequestOrderByPriceDesc() throws Exception {
					//given
					int order = 1;
					StoreSortBy sortBy = StoreSortBy.MIN_PRICE;

					//when & then
					ResultActions actions = mvc.perform(get(BASE_URL)
							.param(pageKey, Integer.toString(page))
							.param(sortByKey, sortBy.value())
							.param(orderKey, Integer.toString(order)))
						.andExpect(status().isOk());

					//then
					Comparator<Store> minPriceDesc = (o1, o2) -> Long.compare(o2.getMinOrderPrice(),
						o1.getMinOrderPrice());
					assertResults(actions, dummies, minPriceDesc, page, size);
				}
			}

			@Nested
			@DisplayName("ORDER_COUNT는 (기본 order = 0)")
			class OrderCount {
				private List<Order> orders;
				private int page;

				@BeforeEach
				void orderSetUp() throws Exception {
					orders = createOrdersWithRandomCount(dummies);
				}

				@Test
				@DisplayName("주문의 개수가 적은것부터 오름차순 정렬이 된다.")
				void testWithPageRequestOrderByOrderCountAscWithDefaultOrder() throws Exception {
					//given
					StoreSortBy sortBy = StoreSortBy.ORDER_COUNT;

					//when & then
					ResultActions actions = mvc.perform(get(BASE_URL)
							.param(pageKey, Integer.toString(page))
							.param(sortByKey, sortBy.value()))
						.andExpect(status().isOk());

					//then
					Comparator<Store> orderCountAsc = (o1, o2) -> {
						long store1OrderCount = orders.stream()
							.filter(o -> o.getStore().getId().equals(o1.getId()))
							.count();

						long store2OrderCount = orders.stream()
							.filter(o -> o.getStore().getId().equals(o2.getId()))
							.count();

						if (store1OrderCount == store2OrderCount) {
							return Long.compare(o1.getId(), o2.getId());
						}
						return Long.compare(store1OrderCount, store2OrderCount);
					};
					assertResults(actions, dummies, orderCountAsc, page, size);
				}

				@Test
				@DisplayName("order가 0이면 주문의 개수가 적은것부터 오름차순 정렬이 된다.")
				void testWithPageRequestOrderByOrderCountAscWithOrderValue() throws Exception {
					//given
					int order = 0;
					StoreSortBy sortBy = StoreSortBy.ORDER_COUNT;

					//when & then
					ResultActions actions = mvc.perform(get(BASE_URL)
							.param(pageKey, Integer.toString(page))
							.param(sortByKey, sortBy.value())
							.param(orderKey, Integer.toString(order)))
						.andExpect(status().isOk());

					//then
					Comparator<Store> orderCountAsc = (o1, o2) -> {
						long store1OrderCount = orders.stream()
							.filter(o -> o.getStore().getId().equals(o1.getId()))
							.count();

						long store2OrderCount = orders.stream()
							.filter(o -> o.getStore().getId().equals(o2.getId()))
							.count();

						if (store1OrderCount == store2OrderCount) {
							return Long.compare(o1.getId(), o2.getId());
						}
						return Long.compare(store1OrderCount, store2OrderCount);
					};
					assertResults(actions, dummies, orderCountAsc, page, size);
				}

				@Test
				@DisplayName("order가 1이면 주문의 개수가 많은것부터 오름차순 정렬이 된다.")
				void testWithPageRequestOrderByOrderCountDescWithOrderValue() throws Exception {
					//given
					int order = 1;
					StoreSortBy sortBy = StoreSortBy.ORDER_COUNT;

					//when & then
					ResultActions actions = mvc.perform(get(BASE_URL)
							.param(pageKey, Integer.toString(page))
							.param(sortByKey, sortBy.value())
							.param(orderKey, Integer.toString(order)))
						.andExpect(status().isOk());

					//then
					Comparator<Store> orderCountDesc = (o1, o2) -> {
						long store1OrderCount = orders.stream()
							.filter(o -> o.getStore().getId().equals(o1.getId()))
							.count();

						long store2OrderCount = orders.stream()
							.filter(o -> o.getStore().getId().equals(o2.getId()))
							.count();

						if (store1OrderCount == store2OrderCount) {
							return Long.compare(o1.getId(), o2.getId());
						}
						return Long.compare(store2OrderCount, store1OrderCount);
					};
					assertResults(actions, dummies, orderCountDesc, page, size);
				}
			}
		}

		@Nested
		@DisplayName("filterBy 처리")
		class FilterBy {
			private final Comparator<Store> orderByIdAsc = (o1, o2) -> Long.compare(o1.getId(), o2.getId());
			private final int page = 0;

			@Nested
			@DisplayName("CATEGORY_NAME은")
			class CategoryName {

				@Test
				@DisplayName("입력받은 category_name의 상점들만 조회할 수 있다.")
				void testWithPageRequestOrderFilterByCategoryName() throws Exception {
					//given
					StoreFilterBy filterBy = StoreFilterBy.CATEGORY_NAME;
					String value = dummies.get(0).getStoreCategory().getName();

					//when
					ResultActions actions = mvc.perform(get(BASE_URL)
							.param(filterByKey, filterBy.value())
							.param(filterValueKey, value))
						.andExpect(status().isOk());

					//then
					Predicate<Store> expectedFilterByCategoryName = (s) -> s.getStoreCategory().getName().equals(value);
					assertResults(actions, dummies, orderByIdAsc, page, size, expectedFilterByCategoryName);
				}
			}

			@Nested
			@DisplayName("MIN_PRICE는")
			class MinPrice {
				@Test
				@DisplayName("입력받은 min_price의 값 이상의 최소 주문 금액을 가진 상점들만 조회할 수 있다.")
				void testWithPageRequestOrderFilterByMinPrice() throws Exception {
					//given
					StoreFilterBy filterBy = StoreFilterBy.MIN_PRICE;
					int minOrderPrice = dummies.get(0).getMinOrderPrice();
					String value = Integer.toString(minOrderPrice);

					//when
					ResultActions actions = mvc.perform(get(BASE_URL)
							.param(filterByKey, filterBy.value())
							.param(filterValueKey, value))
						.andExpect(status().isOk());

					//then
					Predicate<Store> expectedFilterByCategoryName = (s) -> s.getMinOrderPrice() >= minOrderPrice;
					assertResults(actions, dummies, orderByIdAsc, page, size, expectedFilterByCategoryName);
				}
			}
		}

		@Nested
		@DisplayName("오더와 필터 중첩 처리")
		class OrderByAndFilterBy {
			@Test
			@DisplayName("category를 이용해 filter한 뒤, 최소 주문 가격을 기준으로 오름차순 정렬")
			void filterByCategoryNameOrderByMinOrderPriceAsc() throws Exception {
				//given
				int page = 0;
				StoreSortBy sortBy = StoreSortBy.MIN_PRICE;
				StoreFilterBy filterBy = StoreFilterBy.CATEGORY_NAME;
				String filterByValue = dummies.get(0).getStoreCategory().getName();

				//when
				ResultActions actions = mvc.perform(get(BASE_URL)
						.param(sortByKey, sortBy.value())
						.param(filterByKey, filterBy.value())
						.param(filterValueKey, filterByValue))
					.andExpect(status().isOk());

				//then
				Comparator<Store> expectedOrderByMinOrderPrice = (o1, o2) -> Integer.compare(o1.getMinOrderPrice(),
					o2.getMinOrderPrice());
				Predicate<Store> expectedFilterByCategoryName = (s) -> s.getStoreCategory()
					.getName()
					.equals(filterByValue);
				assertResults(actions, dummies, expectedOrderByMinOrderPrice, page, size, expectedFilterByCategoryName);
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

		private void assertResults(ResultActions actions, List<Store> stores, Comparator<Store> comparator,
								   int offset, int size, Predicate<Store> filter) throws
			UnsupportedEncodingException, JsonProcessingException {
			String responseString = actions.andReturn()
				.getResponse().getContentAsString();
			Map<String, Object> responseData = objectMapper.readValue(responseString, Map.class);
			String actualResponseDataString = objectMapper.writeValueAsString(responseData.get("data"));

			List<Store> expectedList = stores.stream()
				.filter(filter)
				.sorted(comparator)
				.skip(offset)
				.limit(size)
				.toList();
			StoreInfoListResponse expectedResponse = StoreInfoListResponse.of(expectedList);
			String expectedResponseString = objectMapper.writeValueAsString(expectedResponse);

			assertThat(actualResponseDataString).isEqualTo(expectedResponseString);
		}
	}
}
