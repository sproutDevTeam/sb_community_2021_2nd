package com.tena.sbcommunity2021.global.interceptors;

import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.service.ArticleService;
import com.tena.sbcommunity2021.global.commons.UserAccount;
import com.tena.sbcommunity2021.global.errors.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.tena.sbcommunity2021.global.errors.ErrorCode.FORBIDDEN;
import static com.tena.sbcommunity2021.global.errors.ErrorCode.UNAUTHORIZED;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

	private final UserAccount userAccount;
	private final ArticleService articleService;

	@Lazy @Autowired
	private PathMatcher pathMatcher;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info("AuthenticationInterceptor.preHandle");

		checkUserAuthenticated(userAccount); // 로그인 여부 체크

		if (hasPathPattern(request, "/**/edit", "/**/delete")) {
			final String requestUri = getRequestUri(request);
			final Long id = Long.valueOf(getPathVariable(request, "id"));

			checkUserCanEdit(requestUri, id); // 권한 체크
		}

		return true;
	}

	private void checkUserAuthenticated(final UserAccount userAccount) {
		log.info("AuthenticationInterceptor.checkUserAuthenticated");

		if (!userAccount.isAuthenticated()) {
			throw new CustomException(UNAUTHORIZED);
		}
	}

	private void checkUserCanEdit(final String requestUri, final Long id) {
		log.info("AuthenticationInterceptor.checkUserCanEdit");

		if (requestUri.startsWith("/articles")) { // 게시물 관련된 수정/삭제 요청
			Article article = articleService.getArticle(id);

			if (!article.isEditableBy(userAccount.getAccountId())) {
				throw new CustomException(FORBIDDEN);
			}
		}
	}

	// Request URI 경로패턴 체크
	private boolean hasPathPattern(HttpServletRequest request, String... patterns) {
		String requestUri = getRequestUri(request);

		//경로가 패턴과 일치하는지 체크
		for (String pattern : patterns) {
			boolean result = pathMatcher.match(pattern, requestUri);
			if (result) {
				return true; //하나라도 일치하면 true 리턴
			}
		}

		return false;
	}

	// Context Path 가 제거된 Request URI 리턴
	private String getRequestUri(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();

		//Context Path 가 존재하면, Request URI 에서 Context Path 제거
		if (contextPath != null && contextPath.length() > 0) {
			requestUri = requestUri.substring(contextPath.length());
		}

		return requestUri;
	}

	// 모든 Path Variable 들을 Map<K, V> 형태로 리턴
	private Map<String, String> getPathVariables(HttpServletRequest request) {
		return (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
	}

	// 특정 Path Variable 값을 String 형태로 리턴
	private String getPathVariable(HttpServletRequest request, String pathVariableName) {
		Map<String, String> pathVariables = getPathVariables(request);
		return pathVariables.get(pathVariableName);
	}

}
