package com.tena.sbcommunity2021.articles.service;

import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.articles.exception.ArticleNotFoundException;
import com.tena.sbcommunity2021.articles.repository.ArticleRepository;
import com.tena.sbcommunity2021.global.errors.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

	@InjectMocks
	private ArticleService articleService;

	@Mock
	private ArticleRepository articleRepository;

	@BeforeEach
	void setUp() {
	}

	private ArticleDto.Save buildDtoToCreate() {
		return ArticleDto.Save.builder()
				.title("제목")
				.content("내용")
				.build();
	}

	private ArticleDto.Save buildDtoToUpdate() {
		return ArticleDto.Save.builder()
				.title("제목수정")
				.content("내용수정")
				.build();
	}

	@Test
	@DisplayName("게시물 작성 성공")
	void createArticle() {
		//given
		final ArticleDto.Save dto = buildDtoToCreate();
		Mockito.when(articleRepository.save(any(Article.class))).thenReturn(dto.toDomain());

		//when
		final Article article = articleService.createArticle(dto);

		//then
		verify(articleRepository, times(1)).save(any(Article.class));
		verify(articleRepository, atLeast(1)).save(any(Article.class));
		verify(articleRepository, atLeastOnce()).save(any(Article.class));
		assertThat(article.getTitle()).isEqualTo(dto.getTitle());
		assertThat(article.getContent()).isEqualTo(dto.getContent());

		//for increasing code coverage
		assertThat(article.getId()).isPositive();
		assertThat(article.getId()).isGreaterThan(0);
	}

	@Test
	@DisplayName("전체 게시물 조회")
	void getArticles() {
		//given
		final ArticleDto.Save dto = buildDtoToCreate();
		final Article existing = dto.toDomain();
		Mockito.when(articleRepository.findAll()).thenReturn(List.of(existing));

		//when
		final List<Article> articles = articleService.getArticles();

		//then
		verify(articleRepository, atLeastOnce()).findAll();
		assertThat(articles).isNotEmpty();
		assertThat(articles.get(0).getTitle()).isEqualTo(dto.getTitle());
		assertThat(articles.get(0).getContent()).isEqualTo(dto.getContent());
	}

	@Test
	@DisplayName("전체 게시물 조회 - 게시물이 하나도 존재하지 않을 경우")
	void getArticles_returnEmptyList() {
		//given
		Mockito.when(articleRepository.findAll()).thenReturn(Collections.emptyList());

		//when
		final List<Article> articles = articleService.getArticles();

		//then
		verify(articleRepository, atLeastOnce()).findAll();
		assertThat(articles).isEmpty();
	}

	@Test
	@DisplayName("게시물 조회 - 존재하는 경우 게시물 리턴")
	void getArticle() {
		//given
		final ArticleDto.Save dto = buildDtoToCreate();
		final Article existing = dto.toDomain();
		Mockito.when(articleRepository.findById(anyLong())).thenReturn(Optional.of(existing));

		//when
		final Article article = articleService.getArticle(anyLong());

		//then
		verify(articleRepository, atLeastOnce()).findById(anyLong());
		assertThat(article.getTitle()).isEqualTo(dto.getTitle());
		assertThat(article.getContent()).isEqualTo(dto.getContent());

		//for increasing code coverage
		assertThat(article.getId()).isPositive();
		assertThat(article.getId()).isGreaterThan(0);
	}

	@Test
	@DisplayName("게시물 조회 - 존재하지 않는 경우 ArticleNotFoundException 발생")
	void getArticle_notFound() {
		//given
		Mockito.when(articleRepository.findById(anyLong())).thenReturn(Optional.empty());

		//then
		ArticleNotFoundException e = assertThrows(ArticleNotFoundException.class, () -> {
			//when
			articleService.getArticle(anyLong());
		});
		verify(articleRepository, atLeastOnce()).findById(anyLong());
		assertThat(e.getErrorCode()).isEqualTo(ErrorCode.ARTICLE_NOT_FOUND);
	}

	@Test
	@DisplayName("게시물 삭제")
	void deleteArticle() {
		//given
		final Article existing = buildDtoToCreate().toDomain();
		Mockito.when(articleRepository.findById(anyLong())).thenReturn(Optional.of(existing));

		//when
		articleService.deleteArticle(anyLong());

		//then
		verify(articleRepository, atLeastOnce()).findById(anyLong());
		verify(articleRepository, atLeastOnce()).delete(any(Article.class));

	}
	
	@Test
	@DisplayName("게시물 수정")
	void updateArticle() {
		//given
		final Article existing = buildDtoToCreate().toDomain();
		Mockito.when(articleRepository.findById(anyLong())).thenReturn(Optional.of(existing));
		log.info("existing {}", existing);

		//when
		final ArticleDto.Save dto = buildDtoToUpdate();
		final Article article = articleService.updateArticle(anyLong(), dto);

		//then
		verify(articleRepository, atLeastOnce()).findById(anyLong());
		assertThat(article.getTitle()).isEqualTo(existing.getTitle());
		assertThat(article.getContent()).isEqualTo(existing.getContent());

		assertThat(article).isSameAs(existing);
		assertThat(article.hashCode()).isEqualTo(existing.hashCode());
		log.info("existing {}", existing);
		log.info("article {}", article);
		log.info("article {}", article.hashCode());
		log.info("existing {}", existing.hashCode());
	}
}