package camp.woowak.lab.web.api.store;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.menu.service.MenuCategoryRegistrationService;
import camp.woowak.lab.menu.service.command.MenuCategoryRegistrationCommand;
import camp.woowak.lab.store.service.StoreMenuRegistrationService;
import camp.woowak.lab.store.service.StoreRegistrationService;
import camp.woowak.lab.store.service.command.StoreMenuRegistrationCommand;
import camp.woowak.lab.store.service.command.StoreRegistrationCommand;
import camp.woowak.lab.web.authentication.LoginVendor;
import camp.woowak.lab.web.authentication.annotation.AuthenticationPrincipal;
import camp.woowak.lab.web.dao.store.StoreDao;
import camp.woowak.lab.web.dto.request.store.MenuCategoryRegistrationRequest;
import camp.woowak.lab.web.dto.request.store.StoreMenuRegistrationRequest;
import camp.woowak.lab.web.dto.request.store.StoreRegistrationRequest;
import camp.woowak.lab.web.dto.response.store.MenuCategoryRegistrationResponse;
import camp.woowak.lab.web.dto.response.store.StoreInfoResponse;
import camp.woowak.lab.web.dto.response.store.StoreMenuRegistrationResponse;
import camp.woowak.lab.web.dto.response.store.StoreRegistrationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StoreApiController {

	private final StoreRegistrationService storeRegistrationService;
	private final StoreMenuRegistrationService storeMenuRegistrationService;
	private final MenuCategoryRegistrationService menuCategoryRegistrationService;
	private final StoreDao storeDao;

	@GetMapping("/stores")
	public StoreInfoResponse getStoreInfos() {
		return storeDao.findAllStoreList();
	}

	@PostMapping("/stores")
	public StoreRegistrationResponse storeRegistration(@AuthenticationPrincipal final LoginVendor loginVendor,
													   final @Valid @RequestBody StoreRegistrationRequest request
	) {
		StoreRegistrationCommand command = mapBy(loginVendor, request);

		Long storeId = storeRegistrationService.storeRegistration(command);
		return new StoreRegistrationResponse(storeId);
	}

	private StoreRegistrationCommand mapBy(LoginVendor loginVendor, StoreRegistrationRequest request) {
		return new StoreRegistrationCommand(
			loginVendor.getId(),
			request.storeName(),
			request.storeAddress(),
			request.storePhoneNumber(),
			request.storeCategoryName(),
			request.storeMinOrderPrice(),
			request.storeStartTime(),
			request.storeEndTime());
	}

	@PostMapping("/stores/{storeId}/menus")
	public StoreMenuRegistrationResponse storeMenuRegistration(final @AuthenticationPrincipal LoginVendor loginVendor,
															   final @PathVariable Long storeId,
															   final @Valid @RequestBody StoreMenuRegistrationRequest request
	) {
		StoreMenuRegistrationCommand command =
			new StoreMenuRegistrationCommand(loginVendor.getId(), storeId, request.menuItems());

		List<Long> menuIds = storeMenuRegistrationService.storeMenuRegistration(command);
		return new StoreMenuRegistrationResponse(menuIds);
	}

	@PostMapping("/stores/{storeId}/category")
	public MenuCategoryRegistrationResponse storeCategoryRegistration(@AuthenticationPrincipal LoginVendor loginVendor,
																	  @PathVariable Long storeId,
																	  @Valid @RequestBody MenuCategoryRegistrationRequest request) {
		MenuCategoryRegistrationCommand command =
			new MenuCategoryRegistrationCommand(loginVendor.getId(), storeId, request.name());
		Long registeredId = menuCategoryRegistrationService.register(command);
		return new MenuCategoryRegistrationResponse(registeredId);
	}
}
