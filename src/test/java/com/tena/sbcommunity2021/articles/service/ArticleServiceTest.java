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
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

	@Mock
	private ModelMapper modelMapper;

	@BeforeEach
	void setUp() {
	}

	@Test
	@DisplayName("게시물 작성 - 정상 플로우")
	void createArticle() {
		//given
		// 작성할 데이터를 담은 게시물 DTO
		final ArticleDto.Save saveDto = ArticleDto.Save.builder()
				.title("제목111")
				.content("내용111")
				.build();

		// DTO 를 도메인 객체로 맵핑 시
		final LocalDateTime regDate = LocalDateTime.of(2021, 11, 11, 11, 11);
		final Article savedArticle = Article.builder()
				.id(1L)
				.title(saveDto.getTitle())
				.content(saveDto.getContent())
				.regDate(regDate)
				.updateDate(regDate)
				.build();
		doReturn(savedArticle).when(modelMapper).map(saveDto, Article.class);

		// 작성된 게시물 조회
		doReturn(Optional.of(savedArticle)).when(articleRepository).findById(anyLong());

		//when
		final Article result = articleService.createArticle(saveDto); // 게시물 작성

		//then
		// 메서드 호출 횟수 검증
		verify(modelMapper, times(1)).map(saveDto, Article.class); // DTO 를 도메인 객체로 맵핑, 1회
		verify(articleRepository, times(1)).save(any(Article.class)); // 게시물 저장, 1회
		verify(articleRepository, times(1)).findById(anyLong()); // 작성된 게시물 조회, 1회
//		verify(articleRepository, atLeastOnce()).findById(anyLong()); // 최소 1회 호출
//		verify(articleRepository, atLeast(1)).findById(anyLong()); // 최소 1회 호출, atLeast(1) == atLeastOnce()

		//for increasing code coverage
		// 리턴값 검증
		assertThat(result.getTitle()).isEqualTo(saveDto.getTitle());
		assertThat(result.getContent()).isEqualTo(saveDto.getContent());
		assertThat(result.getId()).isNotNull();
		assertThat(result.getRegDate()).isNotNull();
		assertThat(result.getUpdateDate()).isNotNull();
		assertThat(result.getRegDate()).isEqualTo(result.getUpdateDate());

		log.info("result {}", result);
		log.info("savedArticle {}", savedArticle);
		log.info("isSameHashCode : {}", result.hashCode() == savedArticle.hashCode());
		log.info("result.title {}", result.getTitle());
		log.info("result.content {}", result.getContent());
		log.info("result.id {}", result.getId());
		log.info("result.regDate {}", result.getRegDate());
		log.info("result.updateDate {}", result.getUpdateDate());

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
		// 기존 게시물
		final LocalDateTime regDate = LocalDateTime.of(2021, 11, 11, 11, 11);
		final Article existing = Article.builder()
				.id(1L)
				.title("제목111")
				.content("내용111")
				.regDate(regDate)
				.updateDate(regDate)
				.build();

		// 기존 게시물 조회
		doReturn(Optional.of(existing)).when(articleRepository).findById(anyLong());

		// 업데이트 할 데이터를 담은 게시물 DTO
		final ArticleDto.Save saveDto = ArticleDto.Save.builder()
				.title("제목222")
				.content("내용222")
				.updateDate(LocalDateTime.of(2021, 12, 12, 12, 12))
				.build();

		// 기존값 변경
		doAnswer(invocation -> {
			existing.setTitle(saveDto.getTitle());
			existing.setContent(saveDto.getContent());
			existing.setUpdateDate(saveDto.getUpdateDate());
			return null;
		}).when(modelMapper).map(saveDto, existing);


		//when
		final Article result = articleService.updateArticle(anyLong(), saveDto); // 게시물 수정

		//then
		// 메서드 호출 횟수 검증
		verify(articleRepository, times(1)).findById(anyLong()); // 기존 게시물 조회, 1회
		verify(modelMapper, times(1)).map(saveDto, existing); // DTO 로 기존 게시물 변경, 1회
		verify(articleRepository, atLeastOnce()).update(any()); // 게시물 업데이트, 1회

		//for increasing code coverage
		// 리턴값 검증
		assertThat(result.getTitle()).isEqualTo(saveDto.getTitle());
		assertThat(result.getContent()).isEqualTo(saveDto.getContent());
		assertThat(result.getTitle()).isEqualTo(existing.getTitle());
		assertThat(result.getContent()).isEqualTo(existing.getContent());
		assertThat(result.getRegDate()).isEqualTo(existing.getRegDate());
		assertThat(result.getUpdateDate()).isEqualTo(existing.getUpdateDate());
		assertNotEquals(result.getRegDate(), result.getUpdateDate());

		assertThat(result).isSameAs(existing);
		assertThat(result.hashCode()).isEqualTo(existing.hashCode());

		log.info("result {}", result);
		log.info("existing {}", existing);
		log.info("isSameHashCode : {}", result.hashCode() == existing.hashCode());
		log.info("result.title {}", result.getTitle());
		log.info("result.content {}", result.getContent());
		log.info("result.id {}", result.getId());
		log.info("result.regDate {}", result.getRegDate());
		log.info("result.updateDate {}", result.getUpdateDate());

	}

	@Test
	@DisplayName("게시물 수정")
	void updateArticle__modelmapper를_사용할때_setter없이_단위테스트를_해야한다면_목킹이번거로움() {
		//given
		final Article existing = mock(Article.class); // 기존 게시물
		final LocalDateTime regDate = LocalDateTime.of(2021, 11, 11, 11, 11);
		when(existing.getRegDate()).thenReturn(regDate);

		when(articleRepository.findById(anyLong())).thenReturn(Optional.of(existing)); // 기존 게시물 조회 시

		final ArticleDto.Save saveDto = mock(ArticleDto.Save.class); // 업데이트 할 데이터를 담은 게시물 DTO
		final LocalDateTime updateDate = LocalDateTime.of(2021, 12, 12, 12, 12);
		when(saveDto.getUpdateDate()).thenReturn(updateDate);
		when(saveDto.getTitle()).thenReturn("제목222");
		when(saveDto.getContent()).thenReturn("내용222");

		doAnswer(invocation -> {
			doReturn(saveDto.getTitle()).when(existing).getTitle();
			doReturn(saveDto.getContent()).when(existing).getContent();
			doReturn(saveDto.getUpdateDate()).when(existing).getUpdateDate();
			return null;
		}).when(modelMapper).map(saveDto, existing);


		//when
		final Article result = articleService.updateArticle(anyLong(), saveDto);

		//then
		// 메서드 호출 횟수 검증
		verify(articleRepository, times(1)).findById(anyLong()); // 기존 게시물 조회, 1회
		verify(modelMapper, times(1)).map(saveDto, existing); // DTO 로 기존 게시물 변경, 1회
		verify(articleRepository, atLeastOnce()).update(any()); // 게시물 업데이트, 1회

		//for increasing code coverage
		// 리턴값 검증
		assertThat(result.getTitle()).isEqualTo(saveDto.getTitle());
		assertThat(result.getContent()).isEqualTo(saveDto.getContent());
		assertThat(result.getTitle()).isEqualTo(existing.getTitle());
		assertThat(result.getContent()).isEqualTo(existing.getContent());
		assertThat(result.getRegDate()).isEqualTo(existing.getRegDate());
		assertThat(result.getUpdateDate()).isEqualTo(existing.getUpdateDate());
		assertNotEquals(result.getRegDate(), result.getUpdateDate());

		assertThat(result).isSameAs(existing);
		assertThat(result.hashCode()).isEqualTo(existing.hashCode());

		log.info("result {}", result);
		log.info("existing {}", existing);
		log.info("isSameHashCode : {}", result.hashCode() == existing.hashCode());
		log.info("result.title {}", result.getTitle());
		log.info("result.content {}", result.getContent());
		log.info("result.id {}", result.getId());
		log.info("result.regDate {}", result.getRegDate());
		log.info("result.updateDate {}", result.getUpdateDate());
	}

}