package com.tena.sbcommunity2021.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

/**
 * 단위 테스트, 마이바티스 Mapper 테스트
 */
@MybatisTest // @Transactional 를 포함하고 있음
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE) // @ActiveProfiles 에 설정한 프로파일 환경의 데이터 소스 적용
@ActiveProfiles("test")
@Disabled
public class RepositoryTest {

	protected ModelMapper modelMapper;

	@BeforeEach
	void setModelMapper() {
		modelMapper = new ModelMapper();
		modelMapper.getConfiguration()
				.setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
				.setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
				.setFieldMatchingEnabled(true).setFieldAccessLevel(PRIVATE);
	}

}
