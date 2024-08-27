package camp.woowak.lab.web.dao.menu;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import camp.woowak.lab.web.dto.response.store.MenuCategoryResponse;

public interface MenuDao {

	Page<MenuCategoryResponse> findAllCategoriesByStoreId(Long storeId, Pageable pageable);
}
