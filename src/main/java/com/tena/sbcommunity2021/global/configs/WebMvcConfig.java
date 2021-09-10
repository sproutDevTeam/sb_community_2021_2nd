package com.tena.sbcommunity2021.global.configs;

import com.tena.sbcommunity2021.global.interceptors.AuthenticationInterceptor;
import com.tena.sbcommunity2021.global.interceptors.CommonInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	private final CommonInterceptor commonInterceptor;
	private final AuthenticationInterceptor authenticationInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(commonInterceptor)
				.addPathPatterns("/**") // 적용 경로 패턴
				.excludePathPatterns("/resource/**") // 제외 경로 패턴
				.excludePathPatterns("/error");

		registry.addInterceptor(authenticationInterceptor)
				.addPathPatterns("/**/new") // 생성
				.addPathPatterns("/**/edit") // 수정
				.addPathPatterns("/**/delete"); // 삭제
	}

}
