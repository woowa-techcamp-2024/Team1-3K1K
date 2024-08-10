package camp.woowak.lab.web.api;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.vendor.exception.DuplicateEmailException;
import camp.woowak.lab.vendor.exception.InvalidCreationException;
import camp.woowak.lab.vendor.service.SignUpVendorService;
import camp.woowak.lab.vendor.service.command.SignUpVendorCommand;
import camp.woowak.lab.web.dto.request.SignUpVendorRequest;
import camp.woowak.lab.web.dto.response.ApiResponse;
import camp.woowak.lab.web.error.ErrorCode;
import jakarta.validation.Valid;

@RestController
public class VendorController {
	private final SignUpVendorService signUpVendorService;

	public VendorController(SignUpVendorService signUpVendorService) {
		this.signUpVendorService = signUpVendorService;
	}

	@PostMapping("/vendors")
	public ResponseEntity<?> signUpVendor(@Valid @RequestBody SignUpVendorRequest request) {
		SignUpVendorCommand command =
			new SignUpVendorCommand(request.name(), request.email(), request.password(), request.phone());
		Long registeredId;
		try {
			registeredId = signUpVendorService.signUp(command);
		} catch (InvalidCreationException e) {
			return ResponseEntity.badRequest()
				.body(ProblemDetail.forStatusAndDetail(
					HttpStatus.BAD_REQUEST, ErrorCode.SIGNUP_INVALID_REQUEST.getMessage()));
		} catch (DuplicateEmailException e) {
			return ResponseEntity.ok(ApiResponse.error(ErrorCode.AUTH_DUPLICATE_EMAIL));
		}
		return ResponseEntity.created(URI.create("/vendors/" + registeredId)).build();
	}
}
