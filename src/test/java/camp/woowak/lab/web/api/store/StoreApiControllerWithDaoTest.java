package camp.woowak.lab.web.api.store;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

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

import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.domain.StoreAddress;
import camp.woowak.lab.store.domain.StoreCategory;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.dto.response.store.StoreInfoResponse;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("StoreApiController 클래스 With Dao")
@Transactional
public class StoreApiControllerWithDaoTest {
	@Autowired
	private PayAccountRepository payAccountRepository;
	@Autowired
	private StoreRepository storeRepository;
	@Autowired
	private StoreCategoryRepository storeCategoryRepository;
	@Autowired
	private VendorRepository vendorRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mvc;

	private List<Store> stores;

	@Nested
	@DisplayName("getStoreInfos메서드: GET /stores")
	class GetStoresInfo {
		private final String BASE_URL = "/stores";

		@BeforeEach
		void setUpDummyStores() throws Exception {
			stores = createDummyStores(10);
		}

		@Test
		@DisplayName("아무런 파라미터가 존재하지 않으면 id기준 오름차순으로 모든 store를 보여준다.")
		void getAllStoresInfoSuccess() throws Exception {
			//given

			//when & then
			ResultActions actions = mvc.perform(get(BASE_URL))
				.andExpect(status().isOk());

			//기본적으로는 ID 오름차순
			assertResults(actions, stores, (o1, o2) -> Long.compare(o1.getId(), o2.getId()));
		}

		void assertResults(ResultActions actions, List<Store> stores, Comparator<Store> comparator) throws
			UnsupportedEncodingException, JsonProcessingException {
			stores.sort(comparator);
			String responseString = actions.andReturn()
				.getResponse().getContentAsString();
			Map<String, Object> responseData = objectMapper.readValue(responseString, Map.class);
			String actualResponseDataString = objectMapper.writeValueAsString(responseData.get("data"));

			StoreInfoResponse expectedResponse = StoreInfoResponse.of(stores);
			String expectedResponseString = objectMapper.writeValueAsString(expectedResponse);

			assertThat(actualResponseDataString).isEqualTo(expectedResponseString);
		}

	}

	private List<Store> createDummyStores(int numberOfStores) {
		List<Store> stores = new ArrayList<>();

		Vendor vendor = createDummyVendor();
		for (int i = 0; i < numberOfStores; i++) {
			StoreCategory storeCategory = createRandomDummyStoreCategory();
			String name = "Store " + (i + 1);
			String address = StoreAddress.DEFAULT_DISTRICT;
			String phoneNumber = "123-456-789" + (i % 10);
			Integer minOrderPrice = 5000 + (new Random().nextInt(10000)) / 1000 * 1000;
			LocalDateTime startTime = LocalDateTime.now().plusHours(new Random().nextInt(10)).withSecond(0).withNano(0);
			LocalDateTime endTime = startTime.plusHours(new Random().nextInt(20) + 1);

			Store store = new Store(vendor, storeCategory, name, address, phoneNumber, minOrderPrice, startTime,
				endTime);
			stores.add(store);
		}

		storeRepository.saveAllAndFlush(stores);
		return stores;
	}

	private Vendor createDummyVendor() {
		PayAccount payAccount = new PayAccount();
		payAccountRepository.saveAndFlush(payAccount);
		return vendorRepository.saveAndFlush(
			new Vendor("VendorName", "email@gmail.com", "Password123!", "010-1234-5678",
				payAccount, new NoOpPasswordEncoder()));
	}

	private StoreCategory createRandomDummyStoreCategory() {
		return storeCategoryRepository.saveAndFlush(new StoreCategory(UUID.randomUUID().toString()));
	}
}
