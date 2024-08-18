package camp.woowak.lab.web.api.menu;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import camp.woowak.lab.menu.service.UpdateMenuStockService;
import camp.woowak.lab.menu.service.command.UpdateMenuStockCommand;
import camp.woowak.lab.web.authentication.LoginVendor;
import camp.woowak.lab.web.authentication.annotation.AuthenticationPrincipal;
import camp.woowak.lab.web.dto.request.menu.UpdateMenuStockRequest;
import camp.woowak.lab.web.dto.response.menu.UpdateMenuStockResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MenuApiController {
	private final UpdateMenuStockService updateMenuStockService;

	public MenuApiController(UpdateMenuStockService updateMenuStockService) {
		this.updateMenuStockService = updateMenuStockService;
	}

	@ResponseStatus(HttpStatus.OK)
	@PutMapping("/menus/stock")
	public UpdateMenuStockResponse updateMenuStock(@AuthenticationPrincipal LoginVendor vendor,
												   @Valid @RequestBody UpdateMenuStockRequest request) {
		UpdateMenuStockCommand cmd = new UpdateMenuStockCommand(request.menuId(), request.stock(), vendor.getId());

		Long updatedId = updateMenuStockService.updateMenuStock(cmd);

		log.info("메뉴 재고 업데이트 완료: menuId={}, newStock={}, vendorId={}", updatedId, request.stock(), vendor.getId());

		return new UpdateMenuStockResponse(updatedId);
	}
}
