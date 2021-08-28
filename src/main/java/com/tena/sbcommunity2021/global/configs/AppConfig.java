package com.tena.sbcommunity2021.global.configs;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.modelmapper.config.Configuration.AccessLevel.*;

@Configuration(proxyBeanMethods = false)
public class AppConfig {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration()
				.setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
				.setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
//				.setSkipNullEnabled(true) // null 필드는 맵핑에서 제외
				.setFieldMatchingEnabled(true).setFieldAccessLevel(PRIVATE); // setter 없이도 private 필드 맵핑 가능

		modelMapper.addConverter(removeNanoSecConverter); // 컨버터 추가

		return modelMapper;
	}

	/**
	 * <h2> [TL;DR] LocalDateTime -> String 타입 변환 시, 나노초 제거 </h2>
	 * <ul>
	 *     <li>
	 *         ModelMapper 필드 맵핑 시, LocalDateTime -> String 타입 변환에 사용할 함수를 구현한 것으로,
	 *         String 타입 변환 시 yyyy-MM-dd hh:mm:ss 로 문자열 포맷이 지정됩니다.
	 *     </li>
	 *     <li>
	 *         Ex) (LocalDateTime) 2021-07-20T11:15:30.777 -> (String) 2021-07-20 11:15:30
	 *     </li>
	 * </ul>
	 */
	private final AbstractConverter<LocalDateTime, String> removeNanoSecConverter = new AbstractConverter<>() {
		@Override
		protected String convert(LocalDateTime source) {
			return source != null ? source.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")) : null;
		}
	};
	/*
		👉 convert() 구현 시 유의사항 : null 체크의 필요성 (source != null)
		- 아래처럼 ModelMapper 맵핑 시 null 이 할당된 필드를 제외하는 옵션이 존재한다. 하지만 이 설정은 컨버터에 적용되지 않는다.
		- modelMapper.getConfiguration().setSkipNullEnabled(true); // 이 설정은 컨버터에는 적용되지 X, 필드 타입이 일치할 경우에 적용된다.
		- 따라서 source != null 과 같이 먼저 해당필드 null 체크 후, source.format(...) 메서드를 호출해야 한다. 그렇지 않을 경우 NPE 이 발생한다.
	 */

}