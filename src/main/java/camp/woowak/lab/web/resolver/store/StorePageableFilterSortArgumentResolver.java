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
			&& parameter.getParameterType().isAssignableFrom(StoreInfoListRequest.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
								  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		int page = extractPage(webRequest);

		StoreSortBy sortBy = extractSortBy(webRequest);

		int order = extractOrder(webRequest);

		StoreFilterBy filterBy = extractFilterBy(webRequest);

		String filterValue = extractFilterValue(webRequest);

		return new StoreInfoListRequest(page, sortBy, order, filterBy, filterValue);
	}

	private String extractFilterValue(NativeWebRequest webRequest) {
		return webRequest.getParameter(StoreInfoListRequestConst.FILTER_VALUE_KEY);
	}

	private StoreFilterBy extractFilterBy(NativeWebRequest webRequest) {
		String filterString = webRequest.getParameter(StoreInfoListRequestConst.FILTER_BY_KEY);
		StoreFilterBy filterBy = StoreFilterBy.getFilterBy(filterString);
		return filterBy;
	}

	private int extractOrder(NativeWebRequest webRequest) {
		String orderString = webRequest.getParameter(StoreInfoListRequestConst.ORDER_KEY);
		return parseOrder(orderString);
	}

	private int parseOrder(String orderString) {
		try {
			return Integer.parseInt(orderString);
		} catch (NumberFormatException e) {
			return StoreInfoListRequestConst.DEFAULT_ORDER;
		}
	}

	private static StoreSortBy extractSortBy(NativeWebRequest webRequest) {
		String categoryString = webRequest.getParameter(StoreInfoListRequestConst.SORT_BY_KEY);
		StoreSortBy sortBy = StoreSortBy.getStoreSortBy(categoryString);
		return sortBy;
	}

	private int extractPage(NativeWebRequest webRequest) {
		String pageParameter = webRequest.getParameter(StoreInfoListRequestConst.PAGE_KEY);
		int page = parsePageNumber(pageParameter);
		return page;
	}

	private int parsePageNumber(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return StoreInfoListRequestConst.DEFAULT_PAGE_NUMBER;
		}
	}
}
