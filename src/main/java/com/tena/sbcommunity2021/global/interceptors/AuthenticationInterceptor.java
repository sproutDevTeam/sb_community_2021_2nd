package com.tena.sbcommunity2021.global.interceptors;

import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.service.ArticleService;
import com.tena.sbcommunity2021.global.commons.UserAccount;
import com.tena.sbcommunity2021.global.errors.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info("AuthenticationInterceptor.preHandle");

		checkUserAuthenticated(userAccount); // 로그인 여부 체크

		final String requestUri = request.getRequestURI();

		if (requestUri.contains("/edit") || requestUri.contains("/delete")) { // 현재 요청이 수정/삭제와 관련된 요청인 경우

			final Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
			System.out.println("pathVariables.get(\"id\") = " + pathVariables.get("id"));
			final Long id = Long.valueOf(pathVariables.get("id"));

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

		if (requestUri.contains("/articles")) { // 게시물 관련된 수정/삭제 요청
			Article article = articleService.getArticle(id);

			if (!article.isEditableBy(userAccount.getAccountId())) {
				throw new CustomException(FORBIDDEN);
			}
		}
	}

}
