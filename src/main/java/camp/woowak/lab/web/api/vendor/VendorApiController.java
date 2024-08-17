package camp.woowak.lab.web.api.vendor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.vendor.service.RetrieveVendorService;
import camp.woowak.lab.vendor.service.SignInVendorService;
import camp.woowak.lab.vendor.service.SignUpVendorService;
import camp.woowak.lab.vendor.service.command.SignInVendorCommand;
import camp.woowak.lab.vendor.service.command.SignUpVendorCommand;
import camp.woowak.lab.web.authentication.LoginVendor;
import camp.woowak.lab.web.dto.request.vendor.SignInVendorRequest;
import camp.woowak.lab.web.dto.request.vendor.SignUpVendorRequest;
import camp.woowak.lab.web.dto.response.vendor.RetrieveVendorResponse;
import camp.woowak.lab.web.dto.response.vendor.SignInVendorResponse;
import camp.woowak.lab.web.dto.response.vendor.SignUpVendorResponse;
import camp.woowak.lab.web.resolver.session.SessionConst;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
public class VendorApiController {
	private final SignUpVendorService signUpVendorService;
	private final SignInVendorService signInVendorService;
	private final RetrieveVendorService retrieveVendorService;

	public VendorApiController(SignUpVendorService signUpVendorService, SignInVendorService signInVendorService,
							   RetrieveVendorService retrieveVendorService) {
		this.signUpVendorService = signUpVendorService;
		this.signInVendorService = signInVendorService;
		this.retrieveVendorService = retrieveVendorService;
	}

	@GetMapping("/vendors")
	@ResponseStatus(HttpStatus.OK)
	public RetrieveVendorResponse retrieveVendors() {
		return new RetrieveVendorResponse(retrieveVendorService.retrieveVendors());
	}

	@PostMapping("/vendors")
	@ResponseStatus(HttpStatus.CREATED)
	public SignUpVendorResponse signUpVendor(@Valid @RequestBody SignUpVendorRequest request) {

		SignUpVendorCommand command =
			new SignUpVendorCommand(request.name(), request.email(), request.password(), request.phone());
		String registeredId = signUpVendorService.signUp(command);
		return new SignUpVendorResponse(registeredId);
	}

	@PostMapping("/vendors/login")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public SignInVendorResponse login(@Valid @RequestBody SignInVendorRequest request, HttpSession session) {
		SignInVendorCommand command = new SignInVendorCommand(request.email(), request.password());
		UUID vendorId = signInVendorService.signIn(command);
		session.setAttribute(SessionConst.SESSION_VENDOR_KEY, new LoginVendor(vendorId));
		return new SignInVendorResponse("success");
	}
}
