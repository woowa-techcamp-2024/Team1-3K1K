package camp.woowak.lab.web.api.customer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.customer.service.SignUpCustomerService;
import camp.woowak.lab.customer.service.command.SignUpCustomerCommand;
import camp.woowak.lab.web.dto.request.customer.SignUpCustomerRequest;
import camp.woowak.lab.web.dto.response.customer.SignUpCustomerResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class CustomerApiController {
	private final SignUpCustomerService signUpCustomerService;

	public CustomerApiController(SignUpCustomerService signUpCustomerService) {
		this.signUpCustomerService = signUpCustomerService;
	}

	@PostMapping("/customers")
	@ResponseStatus(HttpStatus.CREATED)
	public SignUpCustomerResponse signUp(@Valid @RequestBody SignUpCustomerRequest request,
										 HttpServletResponse response) {
		SignUpCustomerCommand command =
			new SignUpCustomerCommand(request.name(), request.email(), request.password(), request.phone());

		String registeredId = signUpCustomerService.signUp(command);

		response.setHeader("Location", "/customers/" + registeredId);

		return new SignUpCustomerResponse(registeredId);
	}
}
