package com.tena.sbcommunity2021.global.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@Slf4j
@Component
public class CommonInterceptor implements HandlerInterceptor {

	// 요청(Request)과 맵핑된 Handler 실행 전 처리사항
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.debug("requestURI: {}", request.getRequestURI());
		log.debug("contextPath: {}", request.getContextPath());

		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			log.info("[preHandle][{}][{}{}][{}][{}]", request.getMethod(), request.getRequestURI(), getParameters(request), handlerMethod.getBeanType().getSimpleName(), handlerMethod.getMethod().getName());
		}

		return true;
	}

	// View 렌더링 전 처리사항
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		log.info("[postHandle][{}][{}{}]", request.getMethod(), request.getRequestURI(), getParameters(request));
	}

	// View 렌더링 후 처리사항, 클라이언트에 최종적인 응답(Response) 전달
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		if (ex != null) { // ※ 참고: 만약 @ExceptionHandler 를 통해 ex 를 처리하고 있다면 null 이 잡힘. 즉 예외가 여기까지 넘어오지 않음
			ex.printStackTrace();
			log.info("[afterCompletion][{}][{}{}][exception: {}]", request.getMethod(), request.getRequestURI(), getParameters(request), ex.getClass().getSimpleName());
		}
	}

	// 파라미터를 쿼리스트링 형태로 리턴
	private String getParameters(HttpServletRequest request) {
		StringBuffer posted = new StringBuffer();
		Enumeration<?> parameterNames = request.getParameterNames();

		if (parameterNames != null) {
			posted.append("?");
		}

		while (parameterNames.hasMoreElements()) {
			if (posted.length() > 1) {
				posted.append("&");
			}

			String parameterName = (String) parameterNames.nextElement();

			posted.append(parameterName + "=");

			// 패스워드와 같은 민감한 값들 별표로
			if (parameterName.contains("password") || parameterName.contains("pass") || parameterName.contains("pwd")) {
				posted.append("*****");
			} else {
				posted.append(request.getParameter(parameterName));
			}
		}

		String ipAddr = getRemoteAddr(request);

		if (ipAddr != null && !ipAddr.equals("")) {
			posted.append("&_psip=" + ipAddr);
		}
		return posted.toString();
	}

	// 클라이언트 IP 주소 리턴
	private String getRemoteAddr(HttpServletRequest request) {
		String ipFromHeader = request.getHeader("X-FORWARDED-FOR");

		if (ipFromHeader != null && ipFromHeader.length() > 0) {
			log.debug("ip from proxy - X-FORWARDED-FOR : " + ipFromHeader);
			return ipFromHeader;
		}

		return request.getRemoteAddr();
	}

}