package camp.woowak.lab.web.api.vendor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.vendor.service.SignUpVendorService;
import camp.woowak.lab.vendor.service.command.SignUpVendorCommand;
import camp.woowak.lab.web.api.utils.APIResponse;
import camp.woowak.lab.web.api.utils.APIUtils;
import camp.woowak.lab.web.dto.request.vendor.SignInVendorRequest;
import camp.woowak.lab.web.dto.request.vendor.SignUpVendorRequest;
import camp.woowak.lab.web.dto.response.vendor.SignUpVendorResponse;
import jakarta.validation.Valid;

@RestController
public class VendorApiController {
	private final SignUpVendorService signUpVendorService;

	public VendorApiController(SignUpVendorService signUpVendorService) {
		this.signUpVendorService = signUpVendorService;
	}

	@PostMapping("/vendors")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<APIResponse<SignUpVendorResponse>> signUpVendor(
		@Valid @RequestBody SignUpVendorRequest request) {
		SignUpVendorCommand command =
			new SignUpVendorCommand(request.name(), request.email(), request.password(), request.phone());
		String registeredId = signUpVendorService.signUp(command);
		return APIUtils.of(HttpStatus.CREATED, new SignUpVendorResponse(registeredId));
	}

	@PostMapping("/vendor/login")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public SignUpVendorResponse login(@Valid @RequestBody SignInVendorRequest request) {
		return null;
	}
}
