package camp.woowak.lab.web.api.customer;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.customer.service.SignUpCustomerService;
import camp.woowak.lab.customer.service.command.SignUpCustomerCommand;
import camp.woowak.lab.web.dto.request.SignUpCustomerRequest;
import jakarta.validation.Valid;

@RestController
public class CustomerController {
	private final SignUpCustomerService signUpCustomerService;

	public CustomerController(SignUpCustomerService signUpCustomerService) {
		this.signUpCustomerService = signUpCustomerService;
	}

	@PostMapping("/customers")
	public ResponseEntity<?> signUp(@Valid @RequestBody SignUpCustomerRequest request) {
		SignUpCustomerCommand command =
			new SignUpCustomerCommand(request.name(), request.email(), request.password(), request.phone());
		Long registeredId;

		registeredId = signUpCustomerService.signUp(command);

		return ResponseEntity.created(URI.create("/customers/" + registeredId)).build();
	}
}
