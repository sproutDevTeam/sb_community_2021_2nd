package com.tena.sbcommunity2021.global.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class CommonInterceptor implements HandlerInterceptor {

	// 요청(Request)과 맵핑된 Handler 실행 전 처리사항
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info("CommonInterceptor.preHandle");
		log.info("{} 를 호출했습니다.", handler.toString());

		return HandlerInterceptor.super.preHandle(request, response, handler);
	}

	// View 렌더링 전 처리사항
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		String viewName = (modelAndView != null) ? modelAndView.getViewName() : null;
		log.info("CommonInterceptor.postHandle");
		log.info("{} 가 종료됐습니다. {} 를 view 로 사용합니다.", handler.toString(), viewName);

		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	// View 렌더링 후 처리사항, 클라이언트에 최종적인 응답(Response) 전달
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		log.info("CommonInterceptor.afterCompletion");

		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}

}
