package com.tena.sbcommunity2021.articles.service;

import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.articles.exception.ArticleNotFoundException;
import com.tena.sbcommunity2021.articles.repository.ArticleRepository;
import com.tena.sbcommunity2021.global.errors.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

	@Test
	@DisplayName("게시물 작성 - 정상 플로우")
	void createArticle() {
		//given
		final ArticleDto.Save dto = mock(ArticleDto.Save.class); // 작성될 내용을 담은 DTO

		final Article mock = mock(Article.class); // DTO 를 도메인 객체로 맵핑 시 리턴할 Mock 객체

		when(dto.toDomain()).thenAnswer(invocation -> { // DTO 를 도메인 객체로 맵핑 시
			when(mock.getId()).thenReturn(1L);
			return mock;
		});

		doReturn(Optional.of(mock)).when(articleRepository).findById(anyLong()); // 작성된 게시물 조회 시

		//when
		final Article result = articleService.createArticle(dto);

		//then
		verify(dto, atLeastOnce()).toDomain(); // DTO 를 도메인 객체로 맵핑
		verify(articleRepository, atLeastOnce()).save(any()); // 게시물 저장
		verify(articleRepository, atLeastOnce()).findById(anyLong()); // 게시물 조회

		// 참고 : 메서드 호출 횟수 검증
		// verify(articleRepository, atLeastOnce()).findById(anyLong()); // 최소 1번 호출
		// verify(articleRepository, atLeast(1)).findById(anyLong()); // atLeast(1) == atLeastOnce()
		// verify(articleRepository, times(1)).findById(anyLong()); // 정확히 1번 호출

		//for increasing code coverage
		assertThat(result.getTitle()).isEqualTo(dto.getTitle());
		assertThat(result.getContent()).isEqualTo(dto.getContent());
		assertThat(result.getId()).isNotNull();

	}

	@Test
	@DisplayName("전체 게시물 조회")
	void getArticles() {
		//given
		final Article existing1 = mock(Article.class);
		final Article existing2 = mock(Article.class);

		when(articleRepository.findAll()).thenReturn(List.of(existing1, existing2));

		//when
		final List<Article> articles = articleService.getArticles();

		//then
		verify(articleRepository, atLeastOnce()).findAll();
		assertThat(articles).isNotEmpty();
		assertThat(articles.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("전체 게시물 조회 - 게시물이 하나도 존재하지 않을 경우")
	void getArticles_returnEmptyList() {
		//given
		when(articleRepository.findAll()).thenReturn(Collections.emptyList());

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
		final Article existing = mock(Article.class);
		when(existing.getId()).thenReturn(1L);
		when(existing.getTitle()).thenReturn("제목");
		when(existing.getContent()).thenReturn("내용");

		when(articleRepository.findById(anyLong())).thenReturn(Optional.of(existing));

		//when
		final Article result = articleService.getArticle(anyLong());

		//then
		verify(articleRepository, atLeastOnce()).findById(anyLong());
		assertThat(result.getTitle()).isEqualTo(existing.getTitle());
		assertThat(result.getContent()).isEqualTo(existing.getContent());

		//for increasing code coverage
		assertThat(result.getId()).isPositive();
		assertThat(result.getId()).isGreaterThan(0);
	}

	@Test
	@DisplayName("게시물 조회 - 존재하지 않는 경우 ArticleNotFoundException 발생")
	void getArticle_notFound() {
		//given
		when(articleRepository.findById(anyLong())).thenReturn(Optional.empty());

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
		final Article existing = mock(Article.class);
		when(existing.getId()).thenReturn(1L);
		when(articleRepository.findById(anyLong())).thenReturn(Optional.of(existing));

		//when
		articleService.deleteArticle(anyLong());

		//then
		verify(articleRepository, atLeastOnce()).findById(anyLong()); // 게시물 조회
		verify(articleRepository, atLeastOnce()).deleteById(anyLong()); // 게시물 삭제
	}
	
	@Test
	@DisplayName("게시물 수정")
	void updateArticle() {
		//given
		final Article existing = mock(Article.class); // 기존 게시물
		
		when(articleRepository.findById(anyLong())).thenReturn(Optional.of(existing)); // 게시물 조회 시

		final ArticleDto.Save dto = mock(ArticleDto.Save.class); // 업데이트 될 내용

		// 기존 게시물 수정 시
		doCallRealMethod().when(existing).updateArticle(any()); // 도메인 객체 변경 메서드 호출 (실제 메서드 호출)
		/*
		doAnswer(invocation -> {
			ArticleDto.Save argument = invocation.getArgument(0, ArticleDto.Save.class);
			argument.setTitle(dto.getTitle());
			argument.setContent(dto.getContent());
			return argument;
		}).when(existing).updateArticle(any()); // 도메인 객체 변경 메서드 (목킹)
		*/

		//when
		final Article result = articleService.updateArticle(anyLong(), dto);

		//then
		verify(articleRepository, atLeastOnce()).findById(anyLong()); // 게시물 조회
		verify(existing, atLeastOnce()).updateArticle(any()); // 도메인 객체 변경
		verify(articleRepository, atLeastOnce()).update(any()); // 게시물 업데이트

		assertThat(result.getTitle()).isEqualTo(existing.getTitle());
		assertThat(result.getContent()).isEqualTo(existing.getContent());

		assertThat(result).isSameAs(existing);
		assertThat(result.hashCode()).isEqualTo(existing.hashCode());
		log.info("result {}", result);
		log.info("existing {}", existing);
		log.info("isSameHashCode : {}", result.hashCode() == existing.hashCode());
	}
}