package camp.woowak.lab.web.api.customer;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.customer.service.RetrieveCustomerService;
import camp.woowak.lab.customer.service.SignInCustomerService;
import camp.woowak.lab.customer.service.SignUpCustomerService;
import camp.woowak.lab.customer.service.command.SignInCustomerCommand;
import camp.woowak.lab.customer.service.command.SignUpCustomerCommand;
import camp.woowak.lab.web.authentication.LoginCustomer;
import camp.woowak.lab.web.dto.request.customer.SignInCustomerRequest;
import camp.woowak.lab.web.dto.request.customer.SignUpCustomerRequest;
import camp.woowak.lab.web.dto.response.customer.RetrieveCustomerResponse;
import camp.woowak.lab.web.dto.response.customer.SignInCustomerResponse;
import camp.woowak.lab.web.dto.response.customer.SignUpCustomerResponse;
import camp.woowak.lab.web.resolver.session.SessionConst;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
public class CustomerApiController {
	private final SignUpCustomerService signUpCustomerService;
	private final SignInCustomerService signInCustomerService;
	private final RetrieveCustomerService retrieveCustomerService;

	public CustomerApiController(SignUpCustomerService signUpCustomerService,
								 SignInCustomerService signInCustomerService,
								 RetrieveCustomerService retrieveCustomerService) {
		this.signUpCustomerService = signUpCustomerService;
		this.signInCustomerService = signInCustomerService;
		this.retrieveCustomerService = retrieveCustomerService;
	}

	@GetMapping("/customers")
	@ResponseStatus(HttpStatus.OK)
	public RetrieveCustomerResponse retrieveAllCustomers() {
		return new RetrieveCustomerResponse(retrieveCustomerService.retrieveAllCustomers());
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

	@PostMapping("/customers/login")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public SignInCustomerResponse login(@RequestBody SignInCustomerRequest request, HttpSession session) {
		SignInCustomerCommand command = new SignInCustomerCommand(request.email(), request.password());

		UUID customerId = signInCustomerService.signIn(command);

		session.setAttribute(SessionConst.SESSION_CUSTOMER_KEY, new LoginCustomer(customerId));

		return new SignInCustomerResponse();
	}
}
