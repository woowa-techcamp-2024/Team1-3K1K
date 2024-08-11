package camp.woowak.lab.web.api;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.customer.exception.AuthenticationException;
import camp.woowak.lab.customer.service.SignInCustomerService;
import camp.woowak.lab.customer.service.SignUpCustomerService;
import camp.woowak.lab.customer.service.command.SignInCustomerCommand;
import camp.woowak.lab.customer.service.command.SignUpCustomerCommand;
import camp.woowak.lab.web.dto.request.SignInCustomerRequest;
import camp.woowak.lab.web.dto.request.SignUpCustomerRequest;
import camp.woowak.lab.web.dto.response.ApiResponse;
import camp.woowak.lab.web.error.ErrorCode;
import jakarta.validation.Valid;

@RestController
public class CustomerController {
	private final SignUpCustomerService signUpCustomerService;
	private final SignInCustomerService signInCustomerService;

	public CustomerController(SignUpCustomerService signUpCustomerService,
							  SignInCustomerService signInCustomerService) {
		this.signUpCustomerService = signUpCustomerService;
		this.signInCustomerService = signInCustomerService;
	}

	@PostMapping("/customers")
	public ResponseEntity<?> signUp(@Valid @RequestBody SignUpCustomerRequest request) {
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

	@PostMapping("/customers/sign-in")
	public ResponseEntity<?> signIn(@RequestBody SignInCustomerRequest request) {
		try {
			signInCustomerService.signIn(new SignInCustomerCommand(request.email(), request.password()));
		} catch (AuthenticationException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.AUTH_INVALID_CREDENTIALS));
		}
		// TODO: JWT 토큰 발급
		return ResponseEntity.ok().build();
	}
}
