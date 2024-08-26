package camp.woowak.lab.web.dto.response.cart;

public record CartResponse(String customerId, Long totalAmount, Long totalPrice) {
}
