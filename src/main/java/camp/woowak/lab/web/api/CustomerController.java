package camp.woowak.lab.web.api;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.customer.service.SignUpCustomerService;
import camp.woowak.lab.customer.service.command.SignUpCustomerCommand;
import camp.woowak.lab.web.dto.request.SignUpCustomerRequest;
import camp.woowak.lab.web.dto.response.ApiResponse;
import camp.woowak.lab.web.error.ErrorCode;

@RestController
public class CustomerController {
	private final SignUpCustomerService signUpCustomerService;

	public CustomerController(SignUpCustomerService signUpCustomerService) {
		this.signUpCustomerService = signUpCustomerService;
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
