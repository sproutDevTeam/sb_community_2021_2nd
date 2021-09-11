package com.tena.sbcommunity2021.global.commons.utils;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class URLHelper {

	/**
	 * Context Path 가 제거된 Request URI 리턴
	 */
	public static String getRequestUri(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();

		//Context Path 가 존재하면, Request URI 에서 Context Path 제거
		if (contextPath != null && contextPath.length() > 0) {
			requestUri = getRequestUriContextPathRemoved(requestUri, contextPath);
		}

		return requestUri;
	}

	/**
	 * Context Path 가 제거된 Request URI 리턴
	 * Ex)
	 * @param requestUri  "/sbc2021/articles/{id}/edit" (String)
	 * @param contextPath "/sbc2021" (String)
	 * @return "/articles/{id}/edit" (String)
	 */
	public static String getRequestUriContextPathRemoved(String requestUri, String contextPath) {
		return requestUri.substring(contextPath.length());
	}

	/**
	 * Request URI 경로패턴 체크
	 * Ex)
	 * @param requestUri "/articles/{id}/edit"
	 * @param patterns   기대하는 경로 패턴을 AntPathPattern 형태의 문자열로 지정, 복수개 지정 가능(VarArgs)
	 * @return Request URI 경로패턴이 patterns 중에 하나라도 일치하면 true 리턴, 하나도 일치하지 않으면 false
	 */
	public static boolean hasPathPattern(String requestUri, String... patterns) {
		PathMatcher pathMatcher = new AntPathMatcher(); // AntPathMatcher 활용

		//경로가 패턴과 일치하는지 체크
		for (String pattern : patterns) {
			boolean result = pathMatcher.match(pattern, requestUri);
			if (result) {
				return true; //하나라도 일치하면 true 리턴
			}
		}

		return false;
	}

	public static boolean hasPathPattern(HttpServletRequest request, String... patterns) {
		String requestUri = getRequestUri(request);

		return hasPathPattern(requestUri, patterns);
	}

	/**
	 * 특정 Path Variable 값을 String 형태로 리턴
	 */
	public static String getPathVariable(HttpServletRequest request, String pathVariableName) {
		Map<String, String> pathVariables = getPathVariables(request);

		return pathVariables.get(pathVariableName);
	}

	/**
	 * 모든 Path Variable 들을 Map<K, V> 형태로 리턴
	 *
	 * Handler : @PutMapping("/product/{productId}/productItem/{productItemId}")
	 * RequestURI : /product/10/productItem/20
	 *
	 * 사용법 :
	 * Map<String, String> pathVariables = URLHelper.getPathVariables(request);
	 * => pathVariables.get("pathVariableName");
	 *
	 * Ex) Long productId = Long.valueOf( pathVariables.get("productId") ); // 10
	 * Ex) Long productItemId = Long.valueOf( pathVariables.get("productItemId") ); // 20
	 *
	 * @return Path Variable 에 해당하는 값들을 Map<K, V> 형태로 리턴
	 */
	public static Map<String, String> getPathVariables(HttpServletRequest request) {
		return (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
	}



	/**
	 * Request URI 의 prefix 에 해당하는 문자열 리턴
	 * Ex)
	 * @param requestUri "/articles/{id}/edit"
	 * @return "articles"
	 */
	public static String getUriPrefix(String requestUri) {
		String prefix = requestUri.substring(1); // 첫번째 슬래쉬(/) 제거

		if (prefix.contains("/")) {
			prefix = prefix.substring(0, prefix.indexOf("/"));
		}

		return prefix;
	}

}