package camp.woowak.lab.web.api.store;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.store.service.StoreRegistrationService;
import camp.woowak.lab.store.service.dto.StoreRegistrationRequest;
import camp.woowak.lab.vendor.domain.Vendor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StoreApiController {

	private final StoreRegistrationService storeRegistrationService;

	// TODO:
	//  1. 인증/인가에 대한 스펙이 정의되어야, Vendor Resolver 로직을 결정할 수 있음
	//  2. SSR, CSR 에 대해 통일해야, API 반환타입을 fix 할 수 있음
	//  3. API 공통 응답 명세에 대한 논의 진행 필요
	@PostMapping("/stores")
	public ResponseEntity<Void> storeRegistration(final Vendor vendor,
												  final @Valid @RequestBody StoreRegistrationRequest request
	) {
		storeRegistrationService.storeRegistration(vendor, request);
		return ResponseEntity.ok().build();
	}

}
