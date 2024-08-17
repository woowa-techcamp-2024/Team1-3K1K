package camp.woowak.lab.web.dao.store;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.order.repository.OrderRepository;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.dto.request.store.StoreInfoListRequest;
import camp.woowak.lab.web.dto.request.store.StoreInfoListRequestConst;
import camp.woowak.lab.web.dto.response.store.StoreInfoListResponse;
import camp.woowak.lab.web.resolver.store.StoreFilterBy;
import camp.woowak.lab.web.resolver.store.StoreSortBy;

@SpringBootTest
@Transactional(readOnly = true)
@DisplayName("StoreDao 클래스")
class StoreDaoTest extends StoreDummiesFixture {
	private final StoreDao storeDao;
	private final int dummyCount;
	private List<Store> dummies;

	@Autowired
	public StoreDaoTest(StoreDao storeDao, StoreRepository storeRepository,
						StoreCategoryRepository storeCategoryRepository,
						VendorRepository vendorRepository, PayAccountRepository payAccountRepository,
						OrderRepository orderRepository,
						CustomerRepository customerRepository) {
		super(storeRepository, storeCategoryRepository, vendorRepository, payAccountRepository, orderRepository,
			customerRepository);
		this.storeDao = storeDao;
		this.dummyCount = 105;
	}

	@BeforeEach
	void setUpDummies() {
		this.dummies = createDummyStores(dummyCount);
	}

	@Nested
	@DisplayName("findAllStoreList 메서드는")
	class FindAllStoreList {
		private final int size = StoreInfoListRequestConst.DEFAULT_PAGE_SIZE;

		@Nested
		@DisplayName("페이징 처리를")
		class Paging {
			@Test
			@DisplayName("Default 사이즈 만큼 요청한 페이지의 컨텐츠가 나온다.(페이지가 모두 찬 경우)")
			void testWithPageRequestOrderByIdAsc() {
				//given
				int page = 1;
				StoreInfoListRequest request = new StoreInfoListRequest(page);

				//when
				StoreInfoListResponse response = storeDao.findAllStoreList(request);

				//then
				assertThat(response).isNotNull();

				//기대되는 결과값을 만들기 위한 조건
				Comparator<Store> comparator = (o1, o2) -> Long.compare(o1.getId(), o2.getId());
				int offset = page * size;
				assertResponse(response, comparator, offset, size);
			}

			@Test
			@DisplayName("Default 사이즈 만큼 요청한 페이지의 컨텐츠가 나온다. (페이지가 모두 차지 않은 경우)")
			void testWithPageRequestOrderByIdAsc_Not_Enough_Stores() {
				//given
				int page = 5;
				StoreInfoListRequest request = new StoreInfoListRequest(page);

				//when
				StoreInfoListResponse response = storeDao.findAllStoreList(request);

				//then
				assertThat(response).isNotNull();

				//기대되는 결과값을 만들기 위한 조건
				Comparator<Store> comparator = (o1, o2) -> Long.compare(o1.getId(), o2.getId());
				int offset = page * size;
				assertResponse(response, comparator, offset, size);
			}
		}

		@Nested
		@DisplayName("sortBy 처리")
		class SortBy {
			@Nested
			@DisplayName("MIN_PRICE는 (기본 order = 0)")
			class MinPrice {
				@Test
				@DisplayName("해당 페이지가 최소 주문 금액 오름차순 정렬되어 나온다.")
				void testWithPageRequestOrderByPriceAscWithDefaultOrder() {
					//given
					StoreSortBy sortBy = StoreSortBy.MIN_PRICE;
					StoreInfoListRequest request = new StoreInfoListRequest(0, sortBy.value());

					//when
					StoreInfoListResponse response = storeDao.findAllStoreList(request);

					//then
					assertThat(response).isNotNull();

					//기대되는 결과값을 만들기 위한 조건
					Comparator<Store> comparator = (o1, o2) -> {
						if (o1.getMinOrderPrice().equals(o2.getMinOrderPrice()))
							return Long.compare(o1.getId(), o2.getId());
						else
							return Long.compare(o1.getMinOrderPrice(), o2.getMinOrderPrice());
					};
					int offset = 0;
					assertResponse(response, comparator, offset, size);
				}

				@Test
				@DisplayName("order = 0을 넣으면 해당 페이지가 최소 주문 금액 오름차순 정렬되어 나온다.")
				void testWithPageRequestOrderByPriceAscWithOrderValue() {
					//given
					int order = 0;
					StoreSortBy sortBy = StoreSortBy.MIN_PRICE;
					StoreInfoListRequest request = new StoreInfoListRequest(0, sortBy.value(), order);

					//when
					StoreInfoListResponse response = storeDao.findAllStoreList(request);

					//then
					assertThat(response).isNotNull();

					//기대되는 결과값을 만들기 위한 조건
					Comparator<Store> comparator = (o1, o2) -> {
						if (o1.getMinOrderPrice().equals(o2.getMinOrderPrice()))
							return Long.compare(o1.getId(), o2.getId());
						else
							return Long.compare(o1.getMinOrderPrice(), o2.getMinOrderPrice());
					};
					int offset = 0;
					assertResponse(response, comparator, offset, size);
				}

				@Test
				@DisplayName("order = 1을 넣으면 해당 페이지가 최소 주문 금액 내림차순 정렬되어 나온다.")
				void testWithPageRequestOrderByPriceDesc() {
					//given
					int order = 1;
					StoreSortBy sortBy = StoreSortBy.MIN_PRICE;
					StoreInfoListRequest request = new StoreInfoListRequest(0, sortBy.value(), order);

					//when
					StoreInfoListResponse response = storeDao.findAllStoreList(request);

					//then
					assertThat(response).isNotNull();

					//기대되는 결과값을 만들기 위한 조건
					Comparator<Store> comparator = (o1, o2) -> {
						if (o1.getMinOrderPrice().equals(o2.getMinOrderPrice()))
							return Long.compare(o1.getId(), o2.getId());
						else
							return Long.compare(o2.getMinOrderPrice(), o1.getMinOrderPrice());
					};
					int offset = 0;
					assertResponse(response, comparator, offset, size);
				}
			}

			@Nested
			@DisplayName("ORDER_COUNT는 (기본 order = 0)")
			class OrderCount {
				private List<Order> orders;

				@BeforeEach
				void orderSetUp() throws Exception {
					orders = createOrdersWithRandomCount(dummies);
				}

				@Test
				@DisplayName("주문의 개수가 적은것부터 오름차순 정렬이 된다.")
				void testWithPageRequestOrderByOrderCountAscWithDefaultOrder() {
					//given
					StoreSortBy sortBy = StoreSortBy.ORDER_COUNT;
					StoreInfoListRequest request = new StoreInfoListRequest(0, sortBy.value());

					//when
					StoreInfoListResponse response = storeDao.findAllStoreList(request);

					//then

					//기댓값에 대한 정렬 기준 작성
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
					int offset = 0;
					assertResponse(response, orderCountAsc, offset, size);
				}

				@Test
				@DisplayName("order가 0이면 주문의 개수가 적은것부터 오름차순 정렬이 된다.")
				void testWithPageRequestOrderByOrderCountAscWithOrderValue() {
					//given
					int order = 0;
					StoreSortBy sortBy = StoreSortBy.ORDER_COUNT;
					StoreInfoListRequest request = new StoreInfoListRequest(0, sortBy.value(), order);

					//when
					StoreInfoListResponse response = storeDao.findAllStoreList(request);

					//then

					//기댓값에 대한 정렬 기준 작성
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
					int offset = 0;
					assertResponse(response, orderCountAsc, offset, size);
				}

				@Test
				@DisplayName("order가 1이면 주문의 개수가 많은것부터 내림차순 정렬이 된다.")
				void testWithPageRequestOrderByOrderCountDescWithOrderValue() {
					//given
					int order = 1;
					StoreSortBy sortBy = StoreSortBy.ORDER_COUNT;
					StoreInfoListRequest request = new StoreInfoListRequest(0, sortBy.value(), order);

					//when
					StoreInfoListResponse response = storeDao.findAllStoreList(request);

					//then
					//기댓값에 대한 정렬 기준 작성
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
					int offset = 0;
					assertResponse(response, orderCountDesc, offset, size);
				}
			}
		}

		@Nested
		@DisplayName("filterBy 처리")
		class FilterBy {
			@Nested
			@DisplayName("CATEGORY_NAME은")
			class CategoryName {
				private Comparator<Store> orderByIdAsc = (o1, o2) -> Long.compare(o1.getId(), o2.getId());

				@Test
				@DisplayName("입력받은 category_name의 상점들만 조회할 수 있다.")
				void testWithPageRequestOrderFilterByCategoryName() {
					//given
					int page = 0;
					StoreFilterBy filterBy = StoreFilterBy.CATEGORY_NAME;
					String value = dummies.get(0).getStoreCategory().getName();
					StoreInfoListRequest request = new StoreInfoListRequest(page, null, 0, filterBy.value(), value);

					//when
					StoreInfoListResponse response = storeDao.findAllStoreList(request);

					//then
					Predicate<Store> expectedFilterByCategoryName = (s) -> s.getStoreCategory().getName().equals(value);
					assertResponse(response, orderByIdAsc, page, size, expectedFilterByCategoryName);
				}

				@Test
				@DisplayName("입력받은 min_price의 값 이상의 최소 주문 금액을 가진 상점들만 조회할 수 있다.")
				void testWithPageRequestOrderFilterByMinPrice() {
					//given
					int page = 0;
					StoreFilterBy filterBy = StoreFilterBy.MIN_PRICE;
					int value = dummies.get(0).getMinOrderPrice();
					StoreInfoListRequest request = new StoreInfoListRequest(page, null, 0, filterBy.value(),
						Integer.toString(value));

					//when
					StoreInfoListResponse response = storeDao.findAllStoreList(request);

					//then
					Predicate<Store> expectedFilterByMinOrderPrice = (s) -> s.getMinOrderPrice() >= value;
					assertResponse(response, orderByIdAsc, page, size, expectedFilterByMinOrderPrice);
				}
			}
		}

		@Nested
		@DisplayName("오더와 필터 중첩 처리")
		class OrderByAndFilterBy {
			@Test
			@DisplayName("category를 이용해 filter한 뒤, 최소 주문 가격을 기준으로 오름차순 정렬")
			void filterByCategoryNameOrderByMinOrderPriceAsc() {
				//given
				int page = 0;
				int order = 0;
				StoreSortBy sortBy = StoreSortBy.MIN_PRICE;
				StoreFilterBy filterBy = StoreFilterBy.CATEGORY_NAME;
				String filterByValue = dummies.get(0).getStoreCategory().getName();
				StoreInfoListRequest request = new StoreInfoListRequest(page, sortBy.value(), order, filterBy.value(), filterByValue);

				//when
				StoreInfoListResponse response = storeDao.findAllStoreList(request);

				//then
				Comparator<Store> orderByMinOrderPrice = (o1, o2) -> Integer.compare(o1.getMinOrderPrice(),
					o2.getMinOrderPrice());
				Predicate<Store> filterByCategoryName = (s) -> s.getStoreCategory().getName().equals(filterByValue);

				assertResponse(response, orderByMinOrderPrice, 0, size, filterByCategoryName);
			}
		}
	}

	private void assertResponse(StoreInfoListResponse response, Comparator<Store> comparator, int offset, int size,
								Predicate<Store> filter) {
		List<StoreInfoListResponse.InfoResponse> stores = response.getStores();

		List<Store> expectedList = dummies.stream()
			.filter(filter)
			.sorted(comparator)
			.skip(offset)
			.limit(size)
			.toList();

		assertThat(stores.size()).isEqualTo(expectedList.size());
		for (int i = 0; i < stores.size(); i++) {
			StoreInfoListResponse.InfoResponse actual = stores.get(i);
			Store expected = expectedList.get(i);
			assertStoresInfo(actual, expected);
		}
	}

	private void assertResponse(StoreInfoListResponse response, Comparator<Store> comparator, int offset, int size) {
		List<StoreInfoListResponse.InfoResponse> stores = response.getStores();

		List<Store> expectedList = dummies.stream()
			.sorted(comparator)
			.skip(offset)
			.limit(size)
			.toList();

		assertThat(stores.size()).isEqualTo(expectedList.size());
		for (int i = 0; i < stores.size(); i++) {
			StoreInfoListResponse.InfoResponse actual = stores.get(i);
			Store expected = expectedList.get(i);
			assertStoresInfo(actual, expected);
		}
	}

	private void assertStoresInfo(StoreInfoListResponse.InfoResponse info1, Store store) {
		assertAll("assertStoreInfo",
			() -> assertThat(info1.isOpen()).isEqualTo(store.isOpen()),
			() -> assertThat(info1.getName()).isEqualTo(store.getName()),
			() -> assertThat(info1.getCategory()).isEqualTo(store.getStoreCategory().getName()),
			() -> assertThat(info1.getMinOrderPrice()).isEqualTo(store.getMinOrderPrice())
		);
	}
}