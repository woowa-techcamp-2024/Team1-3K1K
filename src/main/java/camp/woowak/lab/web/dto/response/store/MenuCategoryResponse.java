package camp.woowak.lab.web.dto.response.store;

import lombok.Getter;

@Getter
public class MenuCategoryResponse {
	private long id;
	private String name;

	public MenuCategoryResponse() {
	}

	public MenuCategoryResponse(long id, String name) {
		this.id = id;
		this.name = name;
	}
}
