package camp.woowak.lab.web.dao.cart;

import java.util.UUID;

import camp.woowak.lab.web.dto.response.CartResponse;

public interface CartDao {
	/**
	 * @throws camp.woowak.lab.cart.exception.NotFoundCartException
	 */
	CartResponse findByCustomerId(UUID customerId);
}
