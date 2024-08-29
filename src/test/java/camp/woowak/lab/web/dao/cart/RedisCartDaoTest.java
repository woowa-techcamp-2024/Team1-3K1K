package camp.woowak.lab.web.dao.cart;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.cart.domain.Cart;
import camp.woowak.lab.cart.repository.CartRepository;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.infra.cache.FakeMenuStockCacheService;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.menu.repository.MenuCategoryRepository;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.order.repository.OrderRepository;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.dao.store.StoreDummiesFixture;
import camp.woowak.lab.web.dto.response.CartResponse;

@SpringBootTest
@Transactional
@TestPropertySources({
	@TestPropertySource(properties = "cart.dao=redis"),
	@TestPropertySource(properties = "cart.repository=redis")
})
class RedisCartDaoTest extends StoreDummiesFixture {
	private final CartRepository cartRepository;
	@Autowired
	private CartDao cartDao;

	@Autowired
	public RedisCartDaoTest(PayAccountRepository payAccountRepository, StoreRepository storeRepository,
							StoreCategoryRepository storeCategoryRepository,
							VendorRepository vendorRepository,
							OrderRepository orderRepository,
							CustomerRepository customerRepository,
							MenuRepository menuRepository,
							MenuCategoryRepository menuCategoryRepository,
							CartRepository cartRepository) {
		super(storeRepository, storeCategoryRepository, vendorRepository, payAccountRepository, orderRepository,
			customerRepository, menuRepository, menuCategoryRepository, new FakeMenuStockCacheService(),
			new NoOpPasswordEncoder());
		this.cartRepository = cartRepository;
	}

	private Customer customer;
	private Store store;
	private MenuCategory menuCategory;
	private List<Menu> menus;

	@BeforeEach
	void setUp() {
		customer = createDummyCustomers(1).get(0);
		store = createDummyStores(1).get(0);
		menuCategory = createDummyMenuCategories(store, 1).get(0);
		menus = createDummyMenus(store, menuCategory, 5);
	}

	@Test
	@DisplayName("findByCustomerId 메서드는 장바구니에 담긴 메뉴의 아이템을 가져온다.")
	void findByCustomerIdTest() {
		//given
		Cart cart = new Cart(customer.getId().toString());
		cartRepository.save(cart);
		menus.stream()
			.forEach(cart::addMenu);
		Cart save = cartRepository.save(cart);

		//when
		CartResponse response = cartDao.findByCustomerId(customer.getId());

		//then
		assertThat(response.getStoreId()).isEqualTo(store.getId());
		assertThat(response.getStoreName()).isEqualTo(store.getName());
		assertThat(response.getMinOrderPrice()).isEqualTo(store.getMinOrderPrice());
	}
}