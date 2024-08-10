package camp.woowak.lab.web.api;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.customer.service.SignUpCustomerService;
import camp.woowak.lab.customer.service.command.SignUpCustomerCommand;
import camp.woowak.lab.vendor.exception.DuplicateEmailException;
import camp.woowak.lab.vendor.exception.InvalidCreationException;
import camp.woowak.lab.vendor.service.SignUpVendorService;
import camp.woowak.lab.vendor.service.command.SignUpVendorCommand;
import camp.woowak.lab.web.dto.request.SignUpCustomerRequest;
import camp.woowak.lab.web.dto.request.SignUpVendorRequest;
import camp.woowak.lab.web.dto.response.ApiResponse;
import camp.woowak.lab.web.error.ErrorCode;

@RestController
@RequestMapping("/auth")
public class AuthController {
	private final SignUpVendorService signUpVendorService;
	private final SignUpCustomerService signUpCustomerService;

	public AuthController(SignUpVendorService signUpVendorService, SignUpCustomerService signUpCustomerService) {
		this.signUpVendorService = signUpVendorService;
		this.signUpCustomerService = signUpCustomerService;
	}

	@PostMapping("/vendors")
	public ResponseEntity<?> signUpVendor(@RequestBody SignUpVendorRequest request) {
		SignUpVendorCommand command =
			new SignUpVendorCommand(request.name(), request.email(), request.password(), request.phone());
		Long registeredId;
		try {
			registeredId = signUpVendorService.signUp(command);
		} catch (InvalidCreationException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.SIGNUP_INVALID_REQUEST));
		} catch (DuplicateEmailException e) {
			return ResponseEntity.ok(ApiResponse.error(ErrorCode.AUTH_DUPLICATE_EMAIL));
		}
		return ResponseEntity.created(URI.create("/vendors/" + registeredId)).build();
	}

	@PostMapping("/customers")
	public ResponseEntity<?> signUpCustomer(@RequestBody SignUpCustomerRequest request) {
		SignUpCustomerCommand command =
			new SignUpCustomerCommand(request.name(), request.email(), request.password(), request.phone());
		Long registeredId;
		try {
			registeredId = signUpCustomerService.signUp(command);
		} catch (camp.woowak.lab.customer.exception.InvalidCreationException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.SIGNUP_INVALID_REQUEST));
		} catch (camp.woowak.lab.customer.exception.DuplicateEmailException e) {
			return ResponseEntity.ok(ApiResponse.error(ErrorCode.AUTH_DUPLICATE_EMAIL));
		}
		return ResponseEntity.created(URI.create("/customers/" + registeredId)).build();
	}
}
