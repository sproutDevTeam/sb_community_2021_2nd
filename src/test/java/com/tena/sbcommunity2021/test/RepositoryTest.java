package com.tena.sbcommunity2021.test;

import org.junit.jupiter.api.Disabled;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

/**
 * 단위 테스트, 마이바티스 Mapper 테스트
 */
@MybatisTest // @Transactional 를 포함하고 있음
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE) // Profile 에 등록된 DB 정보로 대체
@ActiveProfiles("test")
@Disabled
public class RepositoryTest {
}
