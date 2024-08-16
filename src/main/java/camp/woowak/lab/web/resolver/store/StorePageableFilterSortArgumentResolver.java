package camp.woowak.lab.web.resolver.store;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import camp.woowak.lab.web.dto.request.store.StoreInfoListRequest;
import camp.woowak.lab.web.dto.request.store.StoreInfoListRequestConst;
import camp.woowak.lab.web.resolver.store.annotation.StorePFS;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StorePageableFilterSortArgumentResolver implements HandlerMethodArgumentResolver {
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(StorePFS.class)
			&& parameter.getClass().isAssignableFrom(StoreInfoListRequest.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
								  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		String pageParameter = webRequest.getParameter(StoreInfoListRequestConst.PAGE_KEY);
		int page = parsePageNumber(pageParameter);

		return new StoreInfoListRequest(page);
	}

	private int parsePageNumber(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return StoreInfoListRequest.DEFAULT_PAGE_NUMBER;
		}
	}
}
