package camp.woowak.lab.menu.service.command;

import java.util.UUID;

/**
 * @param vendorId    업데이트를 시도하는 vendor의 id
 * @param menuId      업데이트 할 menu의 id
 * @param updatePrice 업데이트 할 가격
 */
public record MenuPriceUpdateCommand(
	UUID vendorId,
	Long menuId,
	int updatePrice
) {
}
