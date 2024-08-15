package camp.woowak.lab.web.dto.response.cart;

/**
 * TODO : 카트가 추가된 뒤 response를 뭘 보내야할지 애매해서 success만 보냄.
 */
public record AddCartResponse(
	boolean success
) {
}
