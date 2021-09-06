package com.tena.sbcommunity2021.articles.service;

import com.tena.sbcommunity2021.articles.domain.Article;
import com.tena.sbcommunity2021.articles.dto.ArticleDto;
import com.tena.sbcommunity2021.articles.exception.ArticleNotCreatedException;
import com.tena.sbcommunity2021.articles.exception.ArticleNotFoundException;
import com.tena.sbcommunity2021.articles.repository.ArticleRepository;
import com.tena.sbcommunity2021.global.errors.ErrorCode;
import com.tena.sbcommunity2021.global.errors.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ArticleServiceTestV2 {

	@InjectMocks
	private ArticleService articleService;

	@Mock
	private ArticleRepository articleRepository;

	@Spy
	private ModelMapper modelMapper;

	@BeforeEach
	void setUp() {
		modelMapper.getConfiguration()
				.setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
				.setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
				.setFieldMatchingEnabled(true).setFieldAccessLevel(PRIVATE);
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

		// 작성된 게시물 조회 시 리턴할 객체
		final LocalDateTime regDate = LocalDateTime.now();
		final Article savedArticle = Article.builder()
				.title(saveDto.getTitle())
				.content(saveDto.getContent())
				.id(1L)
				.regDate(regDate)
				.updateDate(regDate)
				.build();

		// DTO 를 도메인 객체로 맵핑 시
		doReturn(savedArticle).when(modelMapper).map(saveDto, Article.class);

		// 작성된 게시물 조회 시
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
	@DisplayName("게시물 작성 - 실패 플로우")
	void createArticle_fail() {
		//given
		// 작성할 데이터를 담은 게시물 DTO
		final ArticleDto.Save saveDto = ArticleDto.Save.builder()
				.title("제목111")
				.content("내용111")
				.build();

		final LocalDateTime regDate = LocalDateTime.now();
		final Article savedArticle = Article.builder()
				.title(saveDto.getTitle())
				.content(saveDto.getContent())
				.id(1L)
				.regDate(regDate)
				.updateDate(regDate)
				.build();

		// DTO 를 도메인 객체로 맵핑 시
		doReturn(savedArticle).doCallRealMethod().when(modelMapper).map(saveDto, Article.class);

		// 작성된 게시물 조회 시 ArticleNotCreatedException 발생
		doThrow(new ArticleNotCreatedException()).when(articleRepository).findById(any());

		//when
		final ArticleNotCreatedException e = assertThrows(ArticleNotCreatedException.class, () -> {
			articleService.createArticle(saveDto); // 게시물 작성
		});

		//then
		// 메서드 호출 횟수 검증
		verify(articleRepository, times(1)).findById(any()); // 작성된 게시물 조회, 1회

		//for increasing code coverage
		// 리턴값 검증
		assertThat(e.getClass()).isEqualTo(ArticleNotCreatedException.class);
		assertThat(e.getErrorCode()).isEqualTo(ErrorCode.ARTICLE_NOT_CREATED);
		log.info("e = " + e);
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
		final Article existing = Article.builder().title("제목111").content("내용111").regDate(regDate).updateDate(regDate).build();

		// 업데이트 할 데이터를 담은 게시물 DTO
		final LocalDateTime updateDate = LocalDateTime.of(2021, 12, 12, 12, 12);
		final ArticleDto.Save saveDto = ArticleDto.Save.builder().title("제목222").content("내용222").updateDate(updateDate).build();

		doCallRealMethod().when(modelMapper).map(saveDto, existing);
		when(articleRepository.findById(anyLong())).thenReturn(Optional.of(existing)); // 기존 게시물 조회 시

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