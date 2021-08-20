package com.tena.sbcommunity2021.articles.repository;

import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.test.RepositoryTest;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class ArticleRepositoryTest extends RepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    private Article existingArticle;

    @BeforeEach
    void setUp() {
        final Article build = Article.builder()
                .title("제목111")
                .content("내용111")
                .build();

        articleRepository.save(build); // 저장
        existingArticle = articleRepository.findById(build.getId()).get(); // 조회

        log.info("existingArticle : {}", existingArticle);
    }

    @Test
    @DisplayName("게시물 저장 및 조회 테스트 / @BeforeEach 를 통해 저장, 조회한 결과 확인")
    void save_and_findById() {
        assertThat(existingArticle.getId()).isPositive();
        assertThat(existingArticle.getId()).isGreaterThan(0);
        assertThat(existingArticle.getRegDate()).isNotNull();
        assertThat(existingArticle.getUpdateDate()).isNotNull();
        assertEquals("제목111", existingArticle.getTitle());
        assertEquals("내용111", existingArticle.getContent());
    }

    @Test
    @DisplayName("게시물 저장 실패 - 데이터 무결성 위반 예외 발생 / DataIntegrityViolationException")
    void save_fail_DataIntegrityViolationException_occurs() {
        // given
        Article build = Article.builder()
                .title("제목222")
                .content(null) // NOT NULL 칼럼에 값 누락
                .build();

        // then
        DataAccessException e = assertThrows(DataAccessException.class, () -> {
            // when
            articleRepository.save(build);
        });

        assertEquals(DataIntegrityViolationException.class, e.getClass());
    }

    @Test
    @DisplayName("게시물 유무 조회 테스트")
    void existsById_ifExist_returnTrue() {

        final boolean existsById = articleRepository.existsById(existingArticle.getId());
        final boolean existsById2 = articleRepository.existsById(Long.MAX_VALUE);

        assertThat(existsById).isTrue();
        assertThat(existsById2).isFalse();
    }

    @Test
    @DisplayName("게시물 삭제 테스트")
    void deleteById() {
        // when
        articleRepository.deleteById(existingArticle.getId());

        // then
        final boolean existsById = articleRepository.existsById(existingArticle.getId());
        assertThat(existsById).isFalse();

        final Optional<Article> articleById = articleRepository.findById(existingArticle.getId());
        assertEquals(Optional.empty(), articleById);
    }

    @Test
    @DisplayName("게시물 업데이트 테스트")
    void update() throws InterruptedException {
        // 테스트가 밀리초 시점으로 빠르게 이루어져서 데이터 생성 시점과 업데이트 시점의 시간 차를 주기 위함
        Thread.sleep(500);

        // given
        final ArticleDto.Save dto = ArticleDto.Save.builder()
                .title("제목222")
                .content("내용222")
                .build();

        existingArticle.updateArticle(dto);

        // when
        articleRepository.update(existingArticle);

        // then
        final Article found = articleRepository.findById(existingArticle.getId()).get();

        assertEquals(existingArticle.getId(), found.getId());
        assertEquals(existingArticle.getTitle(), found.getTitle());
        assertEquals(existingArticle.getContent(), found.getContent());
        System.out.println("found.getRegDate() = " + found.getRegDate());
        System.out.println("found.getUpdateDate() = " + found.getUpdateDate());
        assertNotEquals(found.getRegDate(), found.getUpdateDate());
    }

    @Test
    @DisplayName("전체 게시물 조회 테스트")
    void findAll() {
        // given
        final Article build = Article.builder()
                .title("제목222")
                .content("내용222")
                .build();
        articleRepository.save(build);

        // when
        final List<Article> articles = articleRepository.findAll();

        // then
        assertTrue(articles.contains(build)); // Article -> @EqualsAndHashCode(of = {"id"})

    }

    @Test
    @DisplayName("[참고] 조회 결과로 Collection 을 리턴할 경우 데이터가 없으면 빈 컬렉션을 리턴")
    void findAll_return_EmptyCollection() {
        // given
        articleRepository.deleteAll();

        // when
        final List<Article> articles = articleRepository.findAll();

        // then
        assertTrue(articles.isEmpty());
        assertThat(articles).isEmpty();
        assertThat(articles).isNotNull();
        assertThat(articles).isEqualTo(Collections.emptyList());
        assertThat(articles).isEqualTo(Lists.emptyList());
        assertThat(articles).isEqualTo(List.of());

        log.info("articles = {} : 조회결과가 없을 경우, 비어있는 리스트를 리턴한다.", articles);
    }

}