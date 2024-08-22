package camp.woowak.lab.web.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
public class ViewController {

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/stores")
	public String stores() {
		return "stores";
	}

	@GetMapping("/stores/{id}")
	public String store(@PathVariable Long id) {
		return "store";
	}
}
