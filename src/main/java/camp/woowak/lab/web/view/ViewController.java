package camp.woowak.lab.web.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import camp.woowak.lab.store.service.StoreDisplayService;
import camp.woowak.lab.store.service.response.StoreDisplayResponse;

@Controller
@RequestMapping("/view")
public class ViewController {
	private StoreDisplayService storeDisplayService;

	public ViewController(StoreDisplayService storeDisplayService) {
		this.storeDisplayService = storeDisplayService;
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/stores")
	public String stores() {
		return "stores";
	}

	@GetMapping("/stores/{id}")
	public String store(@PathVariable Long id, Model model) {
		StoreDisplayResponse storeDisplayResponse = storeDisplayService.displayStore(id);
		model.addAttribute("store", storeDisplayResponse);
		return "store";
	}
}
