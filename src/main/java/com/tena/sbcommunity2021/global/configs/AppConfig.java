package com.tena.sbcommunity2021.global.configs;

import com.tena.sbcommunity2021.global.commons.UserAccount;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration(proxyBeanMethods = false)
public class AppConfig {

	@SessionScope
	@Bean
	public UserAccount userAccount() {
		return new UserAccount();
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration()
				.setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
				.setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
//				.setSkipNullEnabled(true) // null 필드는 맵핑에서 제외
				.setFieldMatchingEnabled(true).setFieldAccessLevel(PRIVATE); // setter 없이도 private 필드 맵핑 가능

		return modelMapper;
	}

}