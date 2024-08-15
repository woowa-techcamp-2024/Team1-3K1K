package camp.woowak.lab.web.api.store;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.menu.service.MenuCategoryRegistrationService;
import camp.woowak.lab.menu.service.command.MenuCategoryRegistrationCommand;
import camp.woowak.lab.store.service.StoreRegistrationService;
import camp.woowak.lab.store.service.dto.StoreRegistrationRequest;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.repository.VendorRepository;
import camp.woowak.lab.web.authentication.LoginVendor;
import camp.woowak.lab.web.authentication.annotation.AuthenticationPrincipal;
import camp.woowak.lab.web.dto.request.store.MenuCategoryRegistrationRequest;
import camp.woowak.lab.web.dto.response.store.MenuCategoryRegistrationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StoreApiController {

	private final StoreRegistrationService storeRegistrationService;
	private final MenuCategoryRegistrationService menuCategoryRegistrationService;
	private final VendorRepository vendorRepository;

	// TODO: 서비스 메서드 코드 스타일 통일하면서 dtoResponse 반환하도록 수정할 예정
	@PostMapping("/stores")
	public ResponseEntity<Void> storeRegistration(@AuthenticationPrincipal final LoginVendor loginVendor,
												  @Valid @RequestBody final StoreRegistrationRequest request
	) {
		Vendor vendor = vendorRepository.findById(loginVendor.getId()).orElseThrow();
		storeRegistrationService.storeRegistration(vendor, request);
		return ResponseEntity.ok().build();
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
