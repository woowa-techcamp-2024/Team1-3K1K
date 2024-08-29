package camp.woowak.lab.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.cart.repository.CartRepository;
import camp.woowak.lab.cart.service.command.AddCartCommand;
import camp.woowak.lab.customer.domain.Customer;
import camp.woowak.lab.customer.repository.CustomerRepository;
import camp.woowak.lab.infra.cache.FakeMenuStockCacheService;
import camp.woowak.lab.menu.domain.Menu;
import camp.woowak.lab.menu.domain.MenuCategory;
import camp.woowak.lab.menu.repository.MenuCategoryRepository;
import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.order.domain.Order;
import camp.woowak.lab.order.exception.DuplicatedOrderException;
import camp.woowak.lab.order.repository.OrderRepository;
import camp.woowak.lab.order.service.OrderCreationService;
import camp.woowak.lab.order.service.command.OrderCreationCommand;
import camp.woowak.lab.payaccount.domain.PayAccount;
import camp.woowak.lab.payaccount.repository.PayAccountRepository;
import camp.woowak.lab.store.domain.Store;
import camp.woowak.lab.store.repository.StoreCategoryRepository;
import camp.woowak.lab.store.repository.StoreRepository;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.NoOpPasswordEncoder;
import camp.woowak.lab.web.dao.store.StoreDummiesFixture;

@SpringBootTest
@TestPropertySources({
	@TestPropertySource(properties = "cart.dao=redis"),
	@TestPropertySource(properties = "cart.repository=redis")
})
@Disabled
public class CartServiceConcurrencyTest extends StoreDummiesFixture {
	private final Logger log = LoggerFactory.getLogger(CartServiceConcurrencyTest.class);

	@Autowired
	public CartServiceConcurrencyTest(PayAccountRepository payAccountRepository,
									  StoreRepository storeRepository,
									  StoreCategoryRepository storeCategoryRepository,
									  VendorRepository vendorRepository,
									  OrderRepository orderRepository,
									  CustomerRepository customerRepository,
									  MenuRepository menuRepository,
									  MenuCategoryRepository menuCategoryRepository,
									  CartRepository cartRepository,
									  CartService cartService,
									  OrderCreationService orderCreationService) {
		super(storeRepository, storeCategoryRepository, vendorRepository, payAccountRepository, orderRepository,
			customerRepository, menuRepository, menuCategoryRepository, new FakeMenuStockCacheService(),
			new NoOpPasswordEncoder());
		this.cartService = cartService;
		this.cartRepository = cartRepository;
		this.orderCreationService = orderCreationService;
		this.payAccountRepository = payAccountRepository;
	}

	private OrderCreationService orderCreationService;
	private final CartService cartService;
	private final CartRepository cartRepository;
	private final PayAccountRepository payAccountRepository;

	private PayAccount payAccount;
	private Customer customer;
	private Store store;
	private MenuCategory menuCategory;
	private List<Menu> menus;

	@BeforeEach
	void setUpDummies() {
		payAccount = createPayAccount(1000000L);
		customer = createDummyCustomer(payAccount);
		store = createDummyStores(1).get(0);
		menuCategory = createDummyMenuCategories(store, 1).get(0);
		menus = createDummyMenus(store, menuCategory, 5);
	}

	@Test
	void test() throws InterruptedException {
		//given
		// 검증을 위해 최초 값 확인
		long originBalance = payAccount.getBalance(); // 현재 구매자의 잔액 확인
		Map<Long, Long> originStocks = new HashMap<>(); // 현재 메뉴의 재고 확인

		// 메뉴를 카트에 저장
		menus.stream()
			.forEach(menu -> {
				originStocks.put(menu.getId(), menu.getStockCount());
				cartService.addMenu(new AddCartCommand(customer.getId().toString(), menu.getId()));
			});

		// 동시성 테스트 : 주문/결제을 동시에 요청한다.
		int numberOfThreads = 10;
		ExecutorService ex = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);
		List<Exception> expectedException = Collections.synchronizedList(
			new ArrayList<>());//동시성 문제로 터진 Exception을 저장할 리스트

		//when
		//같은 사람이 동시에 numberOfThreads 만큼의 요청을 보내는 상황
		long totalStartTime = System.currentTimeMillis();
		for (int i = 0; i < numberOfThreads; i++) {
			ex.submit(() -> {
				long startTime = System.currentTimeMillis();
				try {
					OrderCreationCommand command = new OrderCreationCommand(customer.getId());

					orderCreationService.create(command);
				} catch (Exception e) {
					expectedException.add(e);
				} finally {
					latch.countDown();
					long endTime = System.currentTimeMillis();
					log.info("순번: {}, 소요 시간: {}ms", numberOfThreads - latch.getCount(), endTime - startTime);
				}
			});
		}

		latch.await();
		long totalEndTime = System.currentTimeMillis();
		log.info("{}개 쓰레드의 총 소요시간 : {} ms", numberOfThreads, (totalEndTime - totalStartTime));

		//then
		/**
		 * 1. 주문이 단 한개만 생긴다. 이 주문은 카트에 담긴 메뉴들의 가격의 총 합, requester = customer, order items 가 50개
		 * 2. 사용자의 계좌에서 메뉴의 총 가격이 단 한번만 빠져나갔는지?
		 * 3. 재고가 1~50까지 1개씩만 차감됐는지?
		 */
		verify(numberOfThreads, originBalance, originStocks, expectedException);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void verify(int numberOfThreads, long originBalance, Map<Long, Long> originStocks,
					   List<Exception> expectedException) {
		List<Order> all = orderRepository.findAll();

		long totalPrice = menus.stream()
			.map(Menu::getPrice)
			.reduce(0L, Long::sum);

		PayAccount updated = payAccountRepository.findById(payAccount.getId()).get();
		assertAll("verify values",
				  // 첫번째 주문 빼고는 모두 exception을 발생시켜야함
				  () -> assertThat(expectedException.size()).isEqualTo(numberOfThreads - 1),
				  () -> expectedException
					  .forEach(
						  (exception) -> assertThat(exception).isExactlyInstanceOf(DuplicatedOrderException.class)),
				  //주문은 단 한번만 저장되어야 한다.
				  () -> assertThat(all.size()).isEqualTo(1L),
				  //저장된 order의 requester는 주문한 customer다.,
				  () -> assertThat(all.get(0).getRequester().getId()).isEqualTo(customer.getId()),
				  //주문 이후 Customer의 잔고는 단 한번 차감되어야한다.
				  () -> assertThat(updated.getBalance()).isEqualTo(originBalance - totalPrice),
				  //사용자가 주문했던 메뉴들의 재고수는 단 한번 차감되어야한다.
				  () -> menuRepository.findAll()
					  .forEach(menu -> {
						  assertThat(menu.getStockCount()).isEqualTo(originStocks.get(menu.getId()) - 1);
					  })
		);
	}
}
